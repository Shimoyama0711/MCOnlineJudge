$(function () {
    const login = $("#login");
    const uuid = getCookieFromKey("uuid");

    if (uuid !== undefined) {
        $.ajax({
            url: `/get-user-from-uuid?uuid=${uuid}`,
            type: "GET"
        }).done(function (data) {
            console.log(data);
            const json2 = JSON.parse(JSON.stringify(data));

            console.log(json2);

            if (json2 !== null) {
                if (json2.mcid !== null) {
                    let name = json2.mcid;
                    login.attr("href", `/me`);
                    login.html(`
                        <img alt="${name}" src="https://mc-heads.net/avatar/${uuid}" width="24px" height="24px">
                        ${name} さん
                    `);
                }
            }
        }).fail(function (a, b, c) {
            console.log(a);
            console.log(b);
            console.log(c);
        });
    }
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