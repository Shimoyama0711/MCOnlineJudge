import {serve} from "https://deno.land/std@0.184.0/http/server.ts"
import {serveDir} from "https://deno.land/std@0.184.0/http/file_server.ts"
import {Client} from "https://deno.land/x/mysql@v2.11.0/mod.ts"

const client = await new Client().connect({
    hostname: "127.0.0.1",
    username: "root",
    db: "mconlinejudge",
    password: "BTcfrLkK1FFU"
});

const status405 = new Response("405 Method Not Allowed", {
    status: 405,
    headers: {
        "Content-Type": "text/plain"
    }
});

serve(async (req) => {
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

    if (path === "/get-user-from-email") {
        if (method === "POST") {
            const json = await req.json();
            const email = json.email;

            return await getUserFromEmail(email);
        } else return status405;
    }

    if (path === "/get-user-from-uuid") {
        if (method === "POST") {
            const json = await req.json();
            const uuid = json.uuid;

            return await getUserFromUUID(uuid);
        } else return status405;
    }

    if (path.startsWith("/users/")) {
        const text = await Deno.readTextFile("./public/users.html")
        return new Response(text, {
            status: 200,
            headers: {
                "Content-Type": "text/html"
            }
        });
    }

    // 404 Not Found //
    if (dir.status === 404) {
        const text = await Deno.readTextFile("./public/404.html")
        return new Response(text, {
            status: 404,
            headers: {
                "Content-Type": "text/html"
            }
        });
    }

    return dir;
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

    const result = objects[0];

    if (result === undefined) {
        status = 400;
        msg = "このEメールアドレスは登録されていません";
    } else {
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
            "Content-Type": "text/plain"
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
            "Content-Type": "text/plain"
        }
    });
}

function createUUID(){
    return '########-####-4###-y###-############'.replace(/[#y]/g, function(a) {
        let r = (new Date().getTime() + Math.random() * 16) % 16 | 0, v = a === '#' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}