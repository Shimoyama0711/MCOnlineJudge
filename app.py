from datetime import datetime
from flask import Flask, redirect, request, jsonify, make_response
import json
import mysql.connector
import requests

app = Flask(__name__)


def get_user_from_email(email):
    if len(str(email)) == 0 or str(email) == "undefined":
        return "400 Bad Request", 400, {
            "Content-Type": "text/plain"
        }

    conn = mysql.connector.connect(host="127.0.0.1", user="root", password="BTcfrLkK1FFU")

    cursor = conn.cursor()
    cursor.execute(f"SELECT * FROM mconlinejudge.users WHERE email = '{email}'")

    result = cursor.fetchone()
    output_json = json.dumps(result, default=str, indent=4)

    return make_response(output_json), 200, {
        "Content-Type": "application/json"
    }


def get_user_from_uuid(uuid):
    if len(str(uuid)) == 0 or str(uuid) == "undefined":
        return "400 Bad Request", 400, {
            "Content-Type": "text/plain"
        }

    conn = mysql.connector.connect(host="127.0.0.1", user="root", password="BTcfrLkK1FFU")

    cursor = conn.cursor()
    cursor.execute(f"SELECT * FROM mconlinejudge.users WHERE uuid = '{uuid}'")

    result = cursor.fetchone()
    output_json = json.dumps(result, default=str, indent=4)

    return make_response(output_json), 200, {
        "Content-Type": "application/json"
    }


def get_uuid_from_mcid(mcid):
    if len(str(mcid)) == 0 or str(mcid) == "undefined":
        return "400 Bad Request", 400, {
            "Content-Type": "text/plain"
        }

    response = requests.get(f"https://api.mojang.com/users/profiles/minecraft/{mcid}")

    if response.status_code == 200:
        output_json = json.loads(response.text)

        print("OUTPUT JSON:", output_json)

        return make_response(output_json), 200, {
            "Content-Type": "application/json"
        }
    elif response.status_code == 404:
        return "404 No User Found", 404, {
            "Content-Type": "text/plain"
        }
    else:
        return "400 Bad Request", 400, {
            "Content-Type": "text/plain"
        }


def signup(email, mcid, encrypted, uuid):
    if email is None or mcid is None or encrypted is None or uuid is None:
        return "400 Bad Request", 400, {
            "Content-Type": "text/plain"
        }

    now = datetime.now()
    formatted_date = now.strftime("%Y/%m/%d %H:%M:%S")

    conn = mysql.connector.connect(host="127.0.0.1", user="root", password="BTcfrLkK1FFU")

    cursor = conn.cursor()
    cursor.execute(f"INSERT INTO mconlinejudge.users (email, mcid, password, uuid, created_at) VALUES ('{email}', '{mcid}', '{encrypted}', '{uuid}', '{formatted_date}')")

    result = cursor.fetchone()
    output_json = json.dumps(result, default=str, indent=4)

    return make_response(output_json), 200, {
        "Content-Type": "application/json"
    }


@app.route('/')
def root():
    return redirect("/static/index.html")


@app.route("/signup", methods=["POST"])
def signup_api():
    data = request.data.decode('utf-8')
    print(data)
    data = json.loads(data)
    email = data["email"]
    mcid = data["mcid"]
    encrypted = data["encrypted"]
    uuid = data["uuid"]

    return signup(email, mcid, encrypted, uuid)


@app.route("/get-user-from-email")
def get_user_from_email_api():
    args = request.args
    email = args.get("email")
    return get_user_from_email(email)


@app.route("/get-uuid-from-mcid")
def get_uuid_from_mcid_api():
    args = request.args
    mcid = args.get("mcid")
    return get_uuid_from_mcid(mcid)


@app.route("/get-user-from-uuid")
def get_user_from_uuid_api():
    args = request.args
    uuid = args.get("uuid")
    return get_user_from_uuid(uuid)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=True)
