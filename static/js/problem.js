$(function () {
    const problem = location.pathname.replace("/problem/", "").replace(".html", "");

    replaceDetail(problem);

    // CodeMirror 設定部分 //
    const editor = CodeMirror.fromTextArea(document.getElementById("editor"), {
        mode: "text/x-java",
        lineNumbers: true,
        theme: "default",
        tabSize: 4
    });

    // Cookie の "uuid" が空でなければ提出部分を表示 //
    const uuid = getCookieFromKey("uuid");
    const submitDiv = $("#submit-div");

    if (uuid !== undefined) {
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
        const array = {
            uuid: getCookieFromKey("uuid"),
            problem: location.pathname.replace("/problem/", "").replace(".html", ""),
            date: getFormattedDate(new Date()),
            body: editor.getValue()
        };

        console.log(JSON.stringify(array));

        $.ajax({
            url: "/send-judge",
            type: "POST",
            dataType: "text",
            data: JSON.stringify(array)
        }).done(function() {
            // console.log("Ajax Successfully!");
            submitButton.addClass("disabled");
            submitButton.removeClass("btn-success");
            submitButton.addClass("btn-info");
            submitButton.html(`
                <i class="bi-hourglass-split"></i>
                送信中
            `);
            setTimeout(function() {
                location.href = "/submissions.html";
            }, 1000);
        }).fail(function(a, b, c) {
            console.log(a);
            console.log(b);
            console.log(c);
        });
    });

    // MathJax レンダリング //
    MathJax.typesetPromise();
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

function replaceDetail(problem) {
    const json = {
        problem: problem
    };

    const title = $("#problem-title");
    const time = $("#problem-time");
    const memory = $("#problem-memory");
    const difficulty = $("#problem-difficulty");
    const score = $("#problem-score");

    $.ajax({
        url: "/get-problem-detail",
        type: "POST",
        dataType: "json",
        data: JSON.stringify(json)
    }).done(function (data) {
        const json = JSON.parse(JSON.stringify(data));

        title.text(json["title"]);
        time.text(json["time"]);
        memory.text(json["memory"]);
        difficulty.text(json["difficulty"]);
        score.text(`配点：$${json["score"]}$ 点`);

        const h1 = $("h1:first");
        $("title").text(h1.text());

        MathJax.typesetPromise();
    }).catch(function(a, b, c) {
        console.log(a);
        console.log(b);
        console.log(c);
    });
}

function getFormattedDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');
    const second = String(date.getSeconds()).padStart(2, '0');

    return `${year}/${month}/${day} ${hour}:${minute}:${second}`;
}