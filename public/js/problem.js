$(function () {
    const h1 = $("h1:first");
    $("title").text(h1.text());

    // CodeMirror 設定部分 //
    const editor = CodeMirror.fromTextArea(document.getElementById("editor"), {
        mode: "text/x-java",
        lineNumbers: true,
        theme: "default",
        tabSize: 4
    });

    // MathJax レンダリング //
    MathJax.typesetPromise();

    // Cookie の "uuid" が空でなければ提出部分を表示 //
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

    // ボタンが押されたときの処理 //
    const submitButton = $("#submit-button");

    submitButton.on("click", function () {
        const source = editor.getValue();
        console.log(source);

        $.ajax({
            url: "/judge",
            type: "POST",
            dataType: "text",
            data: source
        }).done(function() {
            location.href = "/submit-list";
        }).fail(function(a, b, c) {
            console.log(a);
            console.log(b);
            console.log(c);
        });
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