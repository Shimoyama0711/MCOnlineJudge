$(function () {
    const emailInput = $("#emailInput");
    const passwordInput = $("#passwordInput");
    const loginButton = $("#loginButton");

    emailInput.on("input", function () {
        if (buttonCheck(emailInput.val(), passwordInput.val())) {
            loginButton.removeClass("disabled");
        } else {
            loginButton.addClass("disabled");
        }
    });

    passwordInput.on("input", function () {
        if (buttonCheck(emailInput.val(), passwordInput.val())) {
            loginButton.removeClass("disabled");
        } else {
            loginButton.addClass("disabled");
        }
    });

    loginButton.on("click", async function () {
        const json = {email: emailInput.val(), encrypted: await sha256(passwordInput.val())}
        const alert = $("#login-alert");

        $.ajax({
            url: "/login",
            type: "POST",
            dataType: "text",
            data: JSON.stringify(json)
        }).done(function () {
            console.log("[Ajax Success]");

            alert.removeClass("alert-danger");
            alert.addClass("alert-success");
            alert.css("display", "");
            alert.html(`
                <i class="bi-check-circle-fill"></i>
                ログインに成功しました
            `);

            const json2 = {email: emailInput.val()};

            $.ajax({
                url: "/get-user-from-email",
                type: "POST",
                dataType: "text",
                data: JSON.stringify(json2)
            }).done(function (data) {
                const json = JSON.parse(data);
                const uuid = json["uuid"];
                document.cookie = `uuid=${uuid}`;
                window.location.href = "/";
            });
        }).fail(function (a, b, c) {
            console.log("[Ajax Failed!]");

            alert.css("display", "")
            alert.html(`
                <i class="bi-exclamation-circle-fill"></i>
                ${a["responseText"]}
            `);

            console.log(a);
            console.log(JSON.stringify(a));
            console.log(b);
            console.log(JSON.stringify(b));
            console.log(c);
            console.log(JSON.stringify(c));
        });
    });

    /*
    $.ajax({
        url: "/get-user-from-uuid",
        type: "POST",
        dataType: "text",
        data: json
    }).done(function (data) {
        const json2 = JSON.parse(data);

        if (json2.mcid !== null) {
            let name = json2.mcid;
            login.attr("href", `/users/${name}`);
            login.text(name + " さん");
        }
    }).fail(function (a, b, c) {
        console.log(a);
        console.log(b);
        console.log(c);
    });
     */
});

async function sha256(data) {
    const encoder = new TextEncoder();
    const dataBuffer = encoder.encode(data);

    const hashBuffer = await crypto.subtle.digest('SHA-256', dataBuffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(byte => byte.toString(16).padStart(2, '0')).join('');
}

function buttonCheck(email, password) {
    const regex = new RegExp("^[a-zA-Z0-9_+-]+(.[a-zA-Z0-9_+-]+)*@([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]*\\.)+[a-zA-Z]{2,}$");
    const b1 = regex.test(email);
    const b2 = password.length >= 6;

    return (b1 && b2);
}