$(function () {
    const userInformationLink = $("#user-information-link");
    const uuid = getCookieFromKey("uuid");

    if (uuid.length > 0) {

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