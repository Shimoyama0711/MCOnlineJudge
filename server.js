import {serveDir} from "https://deno.land/std@0.184.0/http/file_server.ts"
import {Client} from "https://deno.land/x/mysql@v2.11.0/mod.ts"
import { serveTls } from "https://deno.land/std@0.190.0/http/server.ts";

const client = await new Client().connect({
    hostname: "mysql-1.c2b8kou8mtea.ap-northeast-1.rds.amazonaws.com",
    username: "admin",
    db: "mconlinejudge",
    password: "BTcfrLkK1FFU"
});

const status404 = new Response("404 Not Found", {
    status: 404,
    headers: {
        "Content-Type": "text/plain"
    }
});

const status405 = new Response("405 Method Not Allowed", {
    status: 405,
    headers: {
        "Content-Type": "text/plain"
    }
});

const certFile = "./ca.crt";
const keyFile = "./ca.key";

serveTls(async (req) => {
    const dir = await serveDir(req, {
        fsRoot: "public"
    });

    const method = req.method.toUpperCase();
    const url = new URL(req.url);
    const path = url.pathname;

    // API //

    // "/signup" //
    if (path === "/signup") {
        if (method === "POST") {
            const json = await req.json();
            const email = json.email;
            const mcid = json.mcid;
            const encrypted = json.encrypted;
            const uuid = json.uuid;

            return await signup(email, mcid, encrypted, uuid);
        } else return status405;
    }

    // "/login" //
    if (path === "/login") {
        if (method === "POST") {
            const json = await req.json();
            const email = json.email;
            const encrypted = json.encrypted;

            return await login(email, encrypted);
        } else return status405;
    }

    // "/send-judge" //
    if (path === "/send-judge") {
        if (method === "POST") {
            const json = await req.json();
            const uuid = json.uuid;
            const problem = json.problem;
            const date = json.date;
            const body = json.body;

            return await sendJudge(uuid, problem, date, body);
        } else return status405;
    }


    console.log(`PATH: ${path}`);

    if (path === "/get-date") {
        const date = new Date();

        return new Response(date.toLocaleString(), {
            status: 200,
            headers: {
                "Content-Type": "text/plain"
            }
        });
    }

    if (path.startsWith("/get-uuid/")) {
        if (method === "GET") {
            const username = path.split("/")[2];
            const url = `https://api.mojang.com/users/profiles/minecraft/${username}`;
            const response = await fetch(url);

            return ((response.ok) ? new Response(await response.text(), {
                status: 200,
                headers: {
                    "Content-Type": "application/json"
                }
            }) : status404);
        } else return status405;
    }

    if (path === "/get-user-from-email") {
        if (method === "POST") {
            const json = await req.json();
            const email = json.email;

            return await getUserFromEmail(email);
        } else return status405;
    }

    if (path === "/get-user-from-mcid") {
        if (method === "POST") {
            const json = await req.json();
            const mcid = json.mcid;

            return await getUserFromMCID(mcid);
        } else return status405;
    }

    if (path === "/get-user-from-uuid") {
        if (method === "POST") {
            const json = await req.json();
            const uuid = json.uuid;

            return await getUserFromUUID(uuid);
        } else return status405;
    }

    if (path === "/get-problems") {
        if (method === "GET") {
            return await getProblems();
        } else return status405;
    }

    if (path === "/get-problem-detail") {
        if (method === "POST") {
            const json = await req.json();
            const problem = json.problem;

            return await getProblemDetail(problem);
        } else return status405;
    }

    if (path === "/get-all-problems-details") {
        if (method === "GET") {
            return await getAllProblemsDetails();
        } else return status405;
    }

    if (path === "/get-sources-from-uuid") {
        if (method === "POST") {
            const json = await req.json();
            const uuid = json.uuid;

            return await getSourcesFromUUID(uuid);
        } else return status405;
    }

    if (path === "/get-source-from-judge-id") {
        if (method === "POST") {
            const json = await req.json();
            const judgeId = json.judge_id;

            return await getSourceFromJudgeId(judgeId);
        } else return status405;
    }

    if (path === "/me") {
        const text = await Deno.readTextFile("./public/user.html");
        return new Response(text, {
            status: 200,
            headers: {
                "Content-Type": "text/html"
            }
        });
    }

    // FIXME: 一旦ユーザー一覧は閉鎖 //

    /*
    if (path.startsWith("/user/")) {
        const text = await Deno.readTextFile("./public/user.html");
        return new Response(text, {
            status: 200,
            headers: {
                "Content-Type": "text/html"
            }
        });
    }
     */

    if (path.startsWith("/submission/")) {
        const text = await Deno.readTextFile("./public/submission.html");
        return new Response(text, {
            status: 200,
            headers: {
                "Content-Type": "text/html"
            }
        });
    }

    if (path.startsWith("/problem/") && !path.includes(".")) {
        const text = await Deno.readTextFile("./public/problem_template.html");
        return new Response(text, {
            status: 200,
            headers: {
                "Content-Type": "text/html"
            }
        });
    }

    // 404 Not Found //
    if (dir.status === 404) {
        const text = await Deno.readTextFile("./public/error.html")
        return new Response(text, {
            status: 404,
            headers: {
                "Content-Type": "text/html"
            }
        });
    }

    return dir;
}, {
    certFile: certFile,
    keyFile: keyFile
}).then(r => {
    console.log(r);
});

/**
 * @param email Eメールアドレス
 * @param mcid MinecraftのID
 * @param encrypted SHA-256でハッシュ化したパスワード
 * @param uuid MCIDに紐付けられたUUID
 * @returns {Promise<Response>}
 */
async function signup(email, mcid, encrypted, uuid) {
    let status = 200;
    let array = {"uuid": uuid};
    let msg = JSON.stringify(array);

    const result = await client.execute(`INSERT INTO mconlinejudge.users (email, mcid, password, uuid, created_at) VALUES ('${email}', '${mcid}', '${encrypted}', '${uuid}', '${new Date().toLocaleString()}')`).catch(function () {
        status = 400;
        msg = "Bad Request";
    });

    console.log(`RESULT => ${result}`);

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "text/plain"
        }
    });
}

async function login(email, encrypted) {
    let status = 200;
    let msg = "OK";

    const objects = await client.query(`SELECT * FROM mconlinejudge.users WHERE email='${email}'`).catch(function () {
        status = 400;
        msg = "このEメールアドレスは登録されていません";
    });

    if (objects === undefined || objects[0] === undefined) {
        status = 400;
        msg = "このEメールアドレスは登録されていません";
    } else {
        const result = objects[0];
        const check = result["password"];

        if (encrypted !== check) {
            status = 400;
            msg = "パスワードが違います"
        }
    }

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "text/plain"
        }
    });
}

async function sendJudge(uuid, problem, date, body) {
    let status = 200;
    let msg = "OK";

    const url = "http://localhost:8080/judge";

    const data = {
        uuid: uuid,
        problem: problem,
        date: date,
        body: body
    };

    const options = {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    };

    fetch(url, options)
        .then(response => response.json())
        .then(data => {
            console.log("Response: ", data);
        })
        .catch(error => {
            console.error("Error: ", error)
            status = 400;
            msg = "ジャッジ中にエラーが発生しました";
        });

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "application/json"
        }
    });
}

async function getUserFromEmail(email) {
    let status = 200;
    let msg = "";

    const objects = await client.query(`SELECT * FROM mconlinejudge.users WHERE email='${email}'`).catch(function () {
        status = 400;
        msg = "Bad Request";
    });

    const result = objects[0];
    msg = JSON.stringify(result);

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "application/json"
        }
    });
}

async function getUserFromMCID(mcid) {
    let status = 200;
    let msg = "";

    const objects = await client.query(`SELECT * FROM mconlinejudge.users WHERE mcid='${mcid}'`).catch(function () {
        status = 400;
        msg = "Bad Request";
    });

    const result = objects[0];
    msg = JSON.stringify(result);

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "application/json"
        }
    });
}

async function getUserFromUUID(uuid) {
    let status = 200;
    let msg = "";

    const objects = await client.query(`SELECT * FROM mconlinejudge.users WHERE uuid='${uuid}'`).catch(function () {
        status = 400;
        msg = "Bad Request";
    });

    const result = objects[0];
    msg = JSON.stringify(result);

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "application/json"
        }
    });
}

async function getProblems() {
    let status = 200;
    let msg = "";

    const objects = await client.query(`SELECT * FROM mconlinejudge.problems`).catch(function () {
        status = 400;
        msg = "Bad Request";
    });

    msg = JSON.stringify(objects);

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "application/json"
        }
    });
}

async function getProblemDetail(problem) {
    let status = 200;
    let msg = "";

    const objects = await client.query(`SELECT * FROM mconlinejudge.problems WHERE id='${problem}'`).catch(function () {
        status = 400;
        msg = "Bad Request";
    });

    const result = objects[0];
    msg = JSON.stringify(result);

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "application/json"
        }
    });
}

async function getAllProblemsDetails() {
    let status = 200;
    let msg = "";

    const objects = await client.query(`SELECT * FROM mconlinejudge.problems`).catch(function () {
        status = 400;
        msg = "Bad Request";
    });

    msg = JSON.stringify(objects);

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "application/json"
        }
    });
}

async function getSourcesFromUUID(uuid) {
    let status = 200;
    let msg = "";

    const objects = await client.query(`SELECT * FROM mconlinejudge.sources WHERE uuid='${uuid}'`).catch(function () {
        status = 400;
        msg = "Bad Request";
    });

    msg = JSON.stringify(objects);

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "application/json"
        }
    });
}

async function getSourceFromJudgeId(judgeId) {
    let status = 200;
    let msg = "";

    const objects = await client.query(`SELECT * FROM mconlinejudge.sources WHERE judge_id='${judgeId}'`).catch(function () {
        status = 400;
        msg = "Bad Request";
    });

    const result = objects[0];
    msg = JSON.stringify(result);

    return new Response(msg, {
        status: status,
        headers: {
            "Content-Type": "application/json"
        }
    });
}