$(function() {
    addRows();
    applyColor();
});

function addRows() {
    const tr = $("#table tr:last");

    $.ajax({
        url: "/get-problems",
        type: "GET"
    }).done(function (data) {
        const json = JSON.parse(JSON.stringify(data));

        for (let obj of json) {
            const className = obj["id"].replace("/", "-");

            tr.after(`
                <tr>
                    <td class="${className}">
                        <a href="/problem/${obj["id"]}.html">${obj["title"]}</a>
                    </td>
                    <td class="${className}"><code>${obj["time"]}</code></td>
                    <td class="${className}"><code>${obj["memory"]}</code></td>
                    <td class="${className}"><code>${obj["difficulty"]}</code></td>
                    <td class="${className}"><code>${obj["score"]}</code></td>
                </tr>
            `);
        }
    });
}

function applyColor() {
    const uuid = getCookieFromKey("uuid");
    const array = {uuid: uuid};

    $.ajax({
        url: "/get-sources-from-uuid",
        type: "POST",
        dataType: "json",
        data: JSON.stringify(array)
    }).done(function(data) {
        const json = JSON.parse(JSON.stringify(data));
        let ACSet = new Set();

        for (let obj of json) {
            const problem = obj["problem"].replace("/", "-");
            const status = obj["status"];

            if (status === "AC") {
                ACSet.add(problem);
                const row = $(`.${problem}`);
                row.removeClass("table-warning");
                row.addClass("table-success");
            } else {
                if (!ACSet.has(problem))
                    $(`.${problem}`).addClass("table-warning");
            }
        }
    }).fail(function (a, b, c) {
        console.log(a);
        console.log(b);
        console.log(c);
    });
}

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