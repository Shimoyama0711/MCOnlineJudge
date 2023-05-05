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
        // const buf = await Deno.readAll(req.body);

        if (method === "POST") {
            const json = await req.json();
            const email = json.email;
            const password = json.password;

            return await signup(email, password);
        } else return status405;
    }

    console.log(`PATH: ${path}`);

    if (path === "/getdate") {
        const date = new Date();

        return new Response(date.toLocaleString(), {
            status: 200,
            headers: {
                "Content-Type": "text/plain"
            }
        });
    }

    if (path === "/get-user-from-uuid") {
        if (method === "POST") {
            const json = await req.json();
            const uuid = json.uuid;

            return await getUserFromUUID(uuid);
        } else return status405;
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

async function signup(email, password) {
    const encrypt = await SHA256(password);

    const uuid = createUUID();
    let status = 200;
    let array = {"uuid": uuid};
    let msg = JSON.stringify(array);

    await client.execute(`INSERT INTO mconlinejudge.users (email, password, uuid, created_at) VALUES ('${email}', '${encrypt}', '${uuid}', '${new Date().toLocaleString()}')`).catch(function () {
        status = 400;
        msg = "Bad Request";
    });

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

async function SHA256(text) {
    const uint8 = new TextEncoder().encode(text);
    const digest = await crypto.subtle.digest("SHA-256", uint8);
    return Array.from(new Uint8Array(digest)).map(v => v.toString(16).padStart(2, '0')).join('');
}

function createUUID(){
    return '########-####-4###-y###-############'.replace(/[#y]/g, function(a) {
        let r = (new Date().getTime() + Math.random() * 16) % 16 | 0, v = a === '#' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}