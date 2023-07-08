$(function() {
    const judgeId = location.pathname.replace("/submission/", "");
    updateTable(judgeId);
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

// 表を更新する //
function updateTable(judgeId) {
    const uuid = getCookieFromKey("uuid");

    const array1 = {
        uuid: uuid
    };

    const array2 = {
        judge_id: judgeId
    };

    // 提出者変更 //
    $.ajax({
        url: "/get-user-from-uuid",
        type: "POST",
        dataType: "json",
        data: JSON.stringify(array1)
    }).done(function(data) {
        const json = JSON.parse(JSON.stringify(data));
        const tableAuthor = $("#table-author");
        const mcid = json["mcid"];

        tableAuthor.html(`
            <a href="/user/">
                <img src="https://crafatar.com/avatars/${uuid}?overlay=true" alt="${mcid}" width="24" height="24">
                ${mcid}
            </a>
        `);
    }).fail(function(a, b, c) {
        console.log(a);
        console.log(b);
        console.log(c);
    });

    // 最初にすべての問題の詳細を取得 //
    getAllProblemsDetails().done(function (data) {
        const allDetails = JSON.parse(JSON.stringify(data));

        // judgeIdから送信したソースを取得 //
        $.ajax({
            url: "/get-source-from-judge-id",
            type: "POST",
            data: JSON.stringify(array2),
        }).done(function(data) {
            const json = JSON.parse(JSON.stringify(data));

            const h1 = $("#h1");
            $('title').html(`Submission #${json["id"]}`);
            h1.text(`Submission #${json["id"]}`);

            const mainBody = $("#main-body");
            mainBody.text(json["body"]);

            Prism.highlightAll();

            const status = json["status"];

            let title = "";
            let score = "";

            for (let o of allDetails) {
                if (o["id"] === json["problem"]) {
                    title = o["title"];
                    score = o["score"];
                    break;
                }
            }

            let badgeClass = "bg-warning";

            if (status === "AC")
                badgeClass = "bg-success";

            if (status === "Judge...")
                badgeClass = "bg-secondary";

            const tableTitle = $("#table-title");
            const tableDate = $("#table-date");
            const tableStatus = $("#table-status");
            const tableLength = $("#table-length");
            const tableScore = $("#table-score");

            tableTitle.html(`
                    <a href="/problem/${json["problem"]}.html">${title}</a>
                `);

            tableDate.html(`
                    <code>${json["date"]}</code>
                `);

            tableStatus.html(`
                    <span class="badge ${badgeClass}">
                        ${json["status"]}
                    </span>
                `);

            tableLength.html(`
                    <code>${json["body"].length}</code>
                `);

            tableScore.html(`
                    <code>${score}</code>
                `);

            const casesJson = JSON.parse(json["cases"]);
            const keys = Object.keys(casesJson);

            for (let key of keys) {
                const tr = $("#test-cases-table tr:last");

                const value = casesJson[key];

                let badgeClass = "bg-warning";

                if (value === "AC")
                    badgeClass = "bg-success";

                if (value === "Judge...")
                    badgeClass = "bg-secondary";

                tr.after(`
                    <tr>
                        <td>
                            <code>${key}.txt</code>
                        </td>
                        <td style="text-align: center">
                            <span class="badge ${badgeClass}">
                                ${value}
                            </span>
                        </td>
                    </tr>
                `);
            }
        }).fail(function(a, b, c) {
            console.log(a);
            console.log(b);
            console.log(c);
        });
    }).fail(function (a, b, c) {
        console.log(a);
        console.log(b);
        console.log(c);
    });
}

function getAllProblemsDetails() {
    return $.ajax({
        url: "/get-all-problems-details",
        type: "GET",
    });
}