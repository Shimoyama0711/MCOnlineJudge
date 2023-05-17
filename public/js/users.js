$(function () {
    const username = location.pathname.replace("/users/", "");
    const uuid = getCookieFromKey("uuid");

    const h1 = $("#username");
    h1.text(username);

    $.ajax({
        url: "/get-user-from-uuid",
        type: "POST",
        dataType: "text",
        data: JSON.stringify({uuid: uuid})
    }).done(function (data) {
        const json = JSON.parse(data);

        console.log(json);

        const mcid = json["mcid"];
        const email = json["email"];
        const password = json["password"];
        const createdAt = json["created_at"];

        const usernameOutput = $("#username-output");
        const mcidOutput = $("#mcid-output");
        const emailOutput = $("#email-output");
        const passwordOutput = $("#password-output");
        const uuidOutput = $("#uuid-output");
        const createdAtOutput = $("#created-at-output");

        console.log(username);
        console.log(mcid);
        console.log(email);
        console.log(password);
        console.log(uuid);
        console.log(createdAt);

        usernameOutput.text(username);
        mcidOutput.text(mcid);
        emailOutput.text(email);
        passwordOutput.text(password);
        uuidOutput.text(uuid);
        createdAtOutput.text(createdAt);
    });
});

function getCookieFromKey(key) {
    const cookies = document.cookie;
    const array = cookies.split(";");

    let result = undefined;

    for (let i = 0; i < array.length; i++) {
        const content = array[i].split("=");

        if (content[0] === key) {
            result = content[1];
            break;
        }
    }

    return result;
}