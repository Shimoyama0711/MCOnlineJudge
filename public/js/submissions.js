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

    $.ajax({
        url: "/get-sources-from-uuid",
        type: "POST",
        dataType: "",
        data: JSON.stringify(array)
    }).done(function(data) {
        const json = JSON.parse(JSON.stringify(data));

        for (let obj of json) {
            const className = obj["problem"].replace("/", "-");
            const status = obj["status"];
            let badgeClass = "bg-warning";

            if (status === "AC")
                badgeClass = "bg-success";

            if (status === "Judge...")
                badgeClass = "bg-secondary";

            tr.after(`
                <tr class="align-middle">
                    <td class="${className}">
                        <a href="/problem/${obj["problem"]}.html">${obj["title"]}</a>
                    </td>
                    <td class="${className}"><code>${obj["date"]}</code></td>
                    <td style="text-align: center" class="${className}"><code>${obj["body"].length}</code></td>
                    <td style="text-align: center" class="${className}">
                        <span class="badge bg-success">
                            ${obj["status"]}
                        </span>
                    </td>
                    <td style="text-align: center" class="${className}"><code>${obj["score"]}</code></td>
                </tr>
            `);
        }

        console.log(data);

    }).fail(function(a, b, c) {
        console.log(a);
        console.log(b);
        console.log(c);
    });

    const tr = $("#table tr:last");

    $.ajax({
        url: "/get-problems",
        type: "GET"
    }).done(function (data) {
        const json = JSON.parse(JSON.stringify(data));
    });
}