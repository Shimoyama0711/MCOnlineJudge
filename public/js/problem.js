$(function () {
    const h1 = $("h1:first");
    $("title").text(h1.text());

    MathJax.typesetPromise()
        .then(() => {
            // レンダリング完了後の処理をここに記述する（任意）
        })
        .catch((err) => {
            console.error(err);
        });

    let editor = CodeMirror.fromTextArea(document.getElementById("editor"), {
        mode: "text/x-java",
        lineNumbers: true,
        theme: "default",
        tabSize: 4
    });

    const uuid = getCookieFromKey("uuid");
    const submitDiv = $("#submit-div");

    if (uuid !== null) {
        const versions = ["Java 8", "Java 20"];
        const selectVersion = $("#select-version");

        for (let i = 0; i < versions.length; i++) {
            selectVersion.append($("<option>", {
                value: i,
                text: versions[i]
            }));
        }
    } else {
        submitDiv.css("display", "none");
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