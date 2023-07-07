$(function() {
    addRows();
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

function addRows() {
    const uuid = getCookieFromKey("uuid");
    const array = {
        uuid: uuid
    };

    // 最初にすべての問題の詳細を取得 //
    getAllProblemsDetails().done(function (data) {
        const allDetails = JSON.parse(JSON.stringify(data));

        // ユーザーのUUIDから送信したソースを取得 //
        $.ajax({
            url: "/get-sources-from-uuid",
            type: "POST",
            data: JSON.stringify(array),
        }).done(function(data) {
            const json = JSON.parse(JSON.stringify(data));

            for (let obj of json) {
                const tr = $("#table tr:last");

                const className = obj["problem"].replace("/", "-");
                const status = obj["status"];

                let title = "";
                let score = "";

                for (let o of allDetails) {
                    if (o["id"] === obj["problem"]) {
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

                tr.after(`
                <tr class="align-middle">
                    <td class="${className}">
                        <a href="/problem/${obj["problem"]}.html">${title}</a>
                    </td>
                    <td class="${className}"><code>${obj["date"]}</code></td>
                    <td style="text-align: center" class="${className}"><code>${obj["body"].length}</code></td>
                    <td style="text-align: center" class="${className}">
                        <span class="badge ${badgeClass}">
                            ${obj["status"]}
                        </span>
                    </td>
                    <td style="text-align: center" class="${className}"><code>${score}</code></td>
                </tr>
            `);
            }

            console.log(data);
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