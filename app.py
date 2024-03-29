import json
from datetime import datetime
import requests
from flask import Flask, redirect, request, make_response, render_template, url_for, session
from flask_mysqldb import MySQL
import MySQLdb.cursors
import hashlib
import secrets

# import mysql.connector


# アプリ開始
app = Flask(__name__)

# SET APP SECRET KEY #
# app.secret_key = secrets.token_hex(16)
app.secret_key = "0123456789abcdef0123456789abcdef"

print(f"APP SECRET KEY: \u001b[31m{app.secret_key}\u001b[0m")

# DBはMySQLを使う
# app.config["SQLALCHEMY_DATABASE_URI"] = "mysql://root:BTcfrLkK1FFU@localhost:3306/mconlinejudge"
app.config["MYSQL_HOST"] = "localhost"
app.config["MYSQL_USER"] = "root"
app.config["MYSQL_PASSWORD"] = "BTcfrLkK1FFU"
app.config["MYSQL_DB"] = "mconlinejudge"

mysql = MySQL(app)


# NORMAL FUNCTIONS #

def get_sha256(text):
    hash_object = hashlib.sha256()
    hash_object.update(text.encode("UTF-8"))
    return hash_object.hexdigest()


# APP ROUTES #

@app.route("/")
def index():
    return render_template("index.html")


@app.route("/navbar")
def navbar():
    return render_template("navbar.html")


@app.route("/left-sidebar")
def left_sidebar():
    return render_template("left-sidebar.html")


@app.route("/login", methods=["GET", "POST"])
def login():
    msg = ""

    if request.method == "POST":
        email = request.form["email"]
        password = request.form["password"]
        hashed = get_sha256(password)

        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute(f"SELECT * FROM users WHERE email = '{email}' AND password = '{hashed}'")
        user = cursor.fetchone()

        if user:
            session["loggedin"] = True
            session["email"] = user["email"]
            session["mcid"] = user["mcid"]
            session["password"] = user["password"]
            session["uuid"] = user["uuid"]
            session["created_at"] = user["created_at"]
            msg = "ログインに成功しました！"
            return render_template("index.html", msg=msg, display=True, success=True)
        else:
            msg = "メールアドレス または パスワードが間違っています"
            return render_template("login.html", msg=msg, display=True, success=False)

    return render_template("login.html", msg=msg, display=False)


@app.route("/logout")
def logout():
    session.pop("loggedin", None)
    session.pop("email", None)
    session.pop("mcid", None)
    session.pop("password", None)
    session.pop("uuid", None)
    session.pop("created_at", None)
    return redirect(url_for("index"))


@app.route("/signup", methods=["GET", "POST"])
def signup():
    msg = ""

    if request.method == "POST":
        hostname = request.host_url

        email = request.form["email"]
        mcid = request.form["mcid"]
        password = request.form["password"]
        uuid = requests.get(f"{hostname}get-uuid?mcid={mcid}").text
        created_at = datetime.now().strftime("%Y/%m/%d %H:%M:%S")

        # print("email:", email)
        # print("mcid:", mcid)
        # print("password:", password)
        # print("uuid:", uuid)
        # print("created_at:", created_at)

        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute(f"SELECT * FROM users WHERE email = '{email}' OR mcid = '{mcid}'")

        user = cursor.fetchone()

        if user:
            msg = "アカウントが既に存在します"
        else:
            hashed = get_sha256(password)

            cursor.execute(f"INSERT INTO users VALUES ('{email}', '{mcid}', '{hashed}', '{uuid}', '{created_at}')")
            mysql.connection.commit()

            session["loggedin"] = True
            session["email"] = email
            session["mcid"] = mcid
            session["password"] = hashed
            session["uuid"] = uuid
            session["created_at"] = created_at

            msg = "アカウントの作成に成功しました！"

            return render_template("index.html", msg=msg, display=True, success=True)

        return render_template("signup.html", msg=msg, display=True, success=False)

    return render_template("signup.html", msg=msg, display=False, success=False)


@app.route("/user")
def user_page():
    args = request.args
    mcid = args.get("mcid")

    print(f"mcid = {mcid}")

    if mcid is not None:
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute(f"SELECT * FROM users WHERE mcid = '{mcid}'")

        user = cursor.fetchone()

        if user:
            mcid = user["mcid"]
            uuid = user["uuid"]
            created_at = user["created_at"]

            return render_template("user.html", query=True, mcid=mcid, uuid=uuid, created_at=created_at)
        else:
            return render_template("error.html", status=404, msg="User Not Found")
    else:
        try:
            print("Session:", session["mcid"])
            return render_template("user.html", query=False)
        except KeyError:
            return render_template("error.html", status=400, msg="Bad Request"), 400


@app.route("/problem/<path:problem_id>")
def problem_page(problem_id):
    if problem_id == "top":
        solved = []
        unsolved = []

        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)

        cursor.execute(f"SELECT * FROM problems")
        problems = cursor.fetchall()

        if session:
            uuid = session["uuid"]

            cursor.execute(f"SELECT * FROM sources WHERE uuid = '{uuid}'")
            submissions = cursor.fetchall()

            for elem in submissions:
                if elem["status"] == "AC":
                    solved.append(elem["problem"])
                else:
                    unsolved.append(elem["problem"])

            solved = list(set(solved))
            unsolved = list(set(unsolved))

        return render_template(f"problem/top.html", problems=problems, solved=solved, unsolved=unsolved)

    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    cursor.execute(f"SELECT * FROM problems WHERE id = '{problem_id}'")

    detail = cursor.fetchone()

    title = detail["title"]
    time = detail["time"]
    memory = detail["memory"]
    difficulty = detail["difficulty"]
    score = detail["score"]
    problem_statement = detail["problem_statement"]
    conditions = detail["conditions"].replace("@", "\\").split("~")
    input_obj = detail["input"].replace("\\n", "\n")
    output_obj = detail["output"].replace("\\n", "\n")
    hints = detail["hints"].split(",")
    input_examples = detail["input_examples"].split(",")
    output_examples = detail["output_examples"].split(",")
    comments = detail["comments"].split(",")

    return render_template(f"problem/problem_template.html",
                           id=problem_id,
                           title=title,
                           time=time,
                           memory=memory,
                           difficulty=difficulty,
                           score=score,
                           problem_statement=problem_statement,
                           conditions=conditions,
                           input=input_obj,
                           output=output_obj,
                           hints=hints,
                           input_examples=input_examples,
                           output_examples=output_examples,
                           comments=comments)


@app.route("/settings")
def settings():
    return render_template("settings.html")


@app.route("/send-judge", methods=["POST"])
def send_judge():
    json_data = request.json

    send_data = {
        "uuid": session["uuid"],
        "problem": json_data.get("problem"),
        "date": json_data.get("date"),
        "body": json_data.get("body")
    }

    response = requests.post("http://localhost:8080/judge", json=send_data)

    return make_response(response.text), response.status_code


@app.route("/send-mc-judge", methods=["POST"])
def send_mc_judge():
    json_data = request.json

    send_data = {
        "uuid": session["uuid"],
        "problem": json_data.get("problem"),
        "date": json_data.get("date"),
        "body": json_data.get("body")
    }

    response = requests.post("http://localhost:8080/mc-judge", json=send_data)

    return make_response(response.text), response.status_code


@app.route("/submissions")
def submissions_page():
    submissions = []

    if session:
        uuid = session["uuid"]

        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute(f"SELECT * FROM sources WHERE uuid = '{uuid}'")
        submissions = cursor.fetchall()

    return render_template("submissions.html", submissions=submissions)


@app.route("/submission/<path:judge_id>")
def submission_page(judge_id):
    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    cursor.execute(f"SELECT * FROM sources WHERE judge_id = '{judge_id}'")

    submission = cursor.fetchone()

    if submission:
        uuid = submission["uuid"]

        cases = []
        cases_json = json.loads(submission["cases"])

        for key, value in cases_json.items():
            cases.append({"key": key, "value": value})

        cursor.execute(f"SELECT * FROM users WHERE uuid = '{uuid}'")
        user = cursor.fetchone()
        mcid = user["mcid"]

        return render_template("submission.html", submission=submission, mcid=mcid, cases=cases)
    else:
        return render_template("error.html", status=404, msg="Not Found"), 404


@app.route("/practice/<path:practice_id>")
def practice_page(practice_id):
    return render_template(f"practice/{practice_id}.html")



# API #


@app.route("/teapot")
@app.route("/419")
def teapot():
    return render_template("error.html", status=419, msg="I'm a teapot."), 419


@app.route("/get-uuid")
def get_uuid():
    args = request.args
    mcid = str(args.get("mcid"))

    if mcid is None or len(mcid) == 0:
        return make_response("400 Bad Request - Invalid username."), 400, {
            "Content-Type": "text/plain"
        }

    response = requests.get(f"https://api.mojang.com/users/profiles/minecraft/{mcid}")

    if response.status_code == 200:
        json_data = json.loads(response.text)
        return make_response(json_data["id"]), 200, {
            "Content-Type": "text/plain"
        }
    else:
        return make_response("400 Bad Request - Invalid username."), 400, {
            "Content-Type": "text/plain"
        }


@app.route("/get-all-problems")
def get_all_problems():
    cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
    cursor.execute(f"SELECT * FROM problems")

    problems = cursor.fetchall()

    return str(problems), 200, {
        "Content-Type": "application/json"
    }


# @app.route("/get-user-from-email")
# def get_user_from_email_api():
#     args = request.args
#     email = args.get("email")
#     return get_user_from_email(email)
#
#
# @app.route("/get-uuid-from-mcid")
# def get_uuid_from_mcid_api():
#     args = request.args
#     mcid = args.get("mcid")
#     return get_uuid_from_mcid(mcid)
#
#
# @app.route("/get-user-from-uuid")
# def get_user_from_uuid_api():
#     args = request.args
#     uuid = args.get("uuid")
#     return get_user_from_uuid(uuid)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
