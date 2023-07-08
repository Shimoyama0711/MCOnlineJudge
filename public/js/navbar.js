$(function () {
    const login = $("#login");
    const uuid = getCookieFromKey("uuid");
    const json = JSON.stringify({uuid: uuid});

    $.ajax({
        url: "/get-user-from-uuid",
        type: "POST",
        dataType: "text",
        data: json
    }).done(function (data) {
        const json2 = JSON.parse(data);

        if (json2.mcid !== null) {
            let name = json2.mcid;
            login.attr("href", `/me`);
            login.html(`
                <img alt="${name}" src="https://crafatar.com/avatars/${uuid}?overlay=true" width="24px" height="24px">
                ${name} さん
            `);
        }
    }).fail(function (a, b, c) {
        console.log(a);
        console.log(b);
        console.log(c);
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