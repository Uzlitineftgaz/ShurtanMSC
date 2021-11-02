let collectionPointActionsList;
let collectionPointID;
let PAGENUM = 1;
let PAGESIZE = 10;


function goOutFromAction() {
    getAllCollectionPoints()
    document.getElementById('collectionPointActionH1').innerText = "Сборный пункты";
    document.getElementById('addCollectionPointBtn').style.display = 'block';
    document.getElementById('cardTableCollectionPoint').style.display = 'block';
    document.getElementById('miningSelect').style.display = 'block';
    document.getElementById('uppgSelect').style.display = 'block';
    document.getElementById('cardTableCollectionPointAction').style.display = 'none';
    document.getElementById('addCollectionPointActionBtn').style.display = 'none';
    document.getElementById('goOutActionsIcon').style.display = 'none';
}

function clickActionBtn(id) {
    collectionPointID = id
    let actionCollectionPoint = collectionPointsList.find(collectionPoint => collectionPoint.id == id)

    document.getElementById('collectionPointActionH1').innerText = actionCollectionPoint.name;
    document.getElementById('addCollectionPointBtn').style.display = 'none';
    document.getElementById('miningSelect').style.display = 'none';
    document.getElementById('uppgSelect').style.display = 'none';
    document.getElementById('cardTableCollectionPoint').style.display = 'none';
    document.getElementById('cardTableCollectionPointAction').style.display = 'block';
    document.getElementById('addCollectionPointActionBtn').style.display = 'block';
    document.getElementById('goOutActionsIcon').style.display = 'block';

    getActionsByCollectionPoint()
}

function getActionsByCollectionPoint(page, pageSize) {
    let formField = document.getElementById('addOrEditCollectionPointActionForm');
    formField['collectionPointId'].value = collectionPointID;

    if (pageSize === undefined) {
        pageSize = PAGESIZE;
    }
    if (page === undefined) {
        page = PAGENUM;
    }

    let pageNum = page - 1;

    let config = {
        method: 'get',
        url: ''
    };

    config.url = '/api/collection_point/actions/' + collectionPointID + '?page=' + pageNum + '&pageSize=' + pageSize + ''

    axios(config)
        .then(function (response) {
            console.log(response.data)
            if (response.status === 200) {
                collectionPointActionsList = response.data.object
            }
            PAGENUM = response.data.pageNumber + 1;
            document.getElementById("collectionPointActionsTable").innerHTML = createViewTableAction(response.data);
            document.getElementById("totalPages").innerHTML = createViewPaginationAction(response.data.totalPages, PAGENUM);
            document.getElementById("dataTableLengthSelect").innerHTML = createViewDataTableLengthSelect(response.data.totalElements);

        })
        .catch(function (error) {
            console.log(error.response)
        })
}

function resetAndCloseFormAction() {
    document.getElementById('closeFormBtnAction').click();
    document.getElementById('addOrEditCollectionPointActionForm').reset();
}

function addOrEditCollectionPointAction(event) {
    event.preventDefault();
    const formData = new FormData(event.target);

    const data = {}
    formData.forEach((value, key) => (data[key] = value));
    let config = {
        method: '',
        url: '',
        data
    };

    // console.log(data)

    if (data.actionId === "" || data.actionId == null) {
        config.method = 'post';
        config.url = '/api/collection_point/manually/add/action'
    } else {
        config.method = 'put';
        config.url = '/api/collection_point/action/edit'
    }

    axios(config)
        .then(function () {
            resetAndCloseFormAction();
            getActionsByCollectionPoint();
        })
        .catch(function (error) {
            console.log("error.response");
            console.log(error);
        });
}

// document.getElementById('addCollectionPointActionBtn').addEventListener('click', addCollectionPointActionBtn);

function addCollectionPointActionBtn() {
    document.getElementById('addOrEditCollectionPointActionH3').innerText = 'Добавить действие'
    document.getElementById('addOrEditCollectionPointActionBtn').innerText = 'Добавить'
}

function editCollectionPointAction(id) {
    document.getElementById('addOrEditCollectionPointActionH3').innerText = 'Редактировать действие'
    document.getElementById('addOrEditCollectionPointActionBtn').innerText = 'Редактировать'


    let editCollectionPointAction = collectionPointActionsList.find(item => item.actionId == id)
    let formField = document.getElementById('addOrEditCollectionPointActionForm')

    formField['actionId'].value = editCollectionPointAction.actionId;
    formField['expend'].value = editCollectionPointAction.expend;
    formField['pressure'].value = editCollectionPointAction.pressure;
    formField['temperature'].value = editCollectionPointAction.temperature;
    formField['collectionPointId'].value = editCollectionPointAction.collectionPointId;

}

function deleteCollectionPointAction(id) {
    axios.delete("/api/collection_point/delete/action/" + id)
        .then(function (response) {
            // console.log(response.data)
            getActionsByCollectionPoint()
        })
        .catch(function (error) {
            console.log(error.response.data)
            alert(error.response.data.message)
        })
}

function createViewTableAction(data) {
    let out = "";
    data.object.map(action => {
        const createdAtDate = new Date('' + action.createdAt + '');
        const createdAtDayOfMonth = createdAtDate.getDate();
        const createdAtMonth = createdAtDate.getMonth(); // Be careful! January is 0, not 1
        const createdAtYear = createdAtDate.getFullYear();
        const createdAtHours = createdAtDate.getHours();
        const createdAtMins = createdAtDate.getMinutes()
        const createdAtDateString = createdAtDayOfMonth + "-" + (createdAtMonth + 1) + "-" + createdAtYear + " " + createdAtHours + ":" + createdAtMins;

        out += "<tr class=\"action_table_row\">\n" +
            "   <td class=\"sorting_1\">" + action.actionId + "</td>\n" +
            "    <td>" + action.expend + "</td>\n" +
            "    <td>" + action.pressure + "</td>\n" +
            "    <td>" + action.temperature + "</td>\n" +
            "     <td id=\"collectionPointIdTd\" hidden value='" + action.collectionPointId + "'>" + action.collectionPointId + "</td>\n" +
            "    <td>" + createdAtDateString + "</td>\n" +
            "     <td><button data-target=\"#exampleModalCenterAction\" data-toggle=\"modal\" class='btn btn-success ml-1 mt-1' id='btn-edit-action' value='" + action.actionId + "' onclick='editCollectionPointAction(this.value)'>Редактировать</button>\n" +
            "      <button class='btn btn-danger ml-1 mt-1' id='btn-edit-action' value='" + action.actionId + "' onclick='deleteCollectionPointAction(this.value)'>Удалить</button></td>\n" +
            "   </tr>"
    })

    return out;
}

function createViewPaginationAction(totalPages, pageNumber) {
    let li = "";
    if (pageNumber === 1) {
        li = "<li class=\"paginate_button page-item previous active \"\n" +
            "    id=\"dataTable_previousAction\"><button disabled aria-controls=\"dataTable\"\n" +
            "   data-dt-idx=\"0\" tabindex=\"0\"\n" +
            "   class=\"page-link\">Previous</button>\n" +
            "   </li>";
    } else {
        li = "<li class=\"paginate_button page-item previous \"\n" +
            "    id=\"dataTable_previousAction\"><button value='\" + 0 + \"' onclick='getActionsByCollectionPoint(PAGENUM-1)' \" aria-controls=\"dataTable\"\n" +
            "   data-dt-idx=\"0\" tabindex=\"0\"\n" +
            "   class=\"page-link\">Previous</button>\n" +
            "   </li>";
    }

    for (let i = 1; i <= totalPages; i++) {
        if (i === pageNumber || i === 0) {
            li += "<li class=\"paginate_button page-item active\"><button value='" + i + "' onclick='getActionsByCollectionPoint(this.value)' " +
                " href=\"#\"\n" +
                "  aria-controls=\"dataTable\"\n" +
                "  data-dt-idx=" + i + "\n" +
                " tabindex=\"0\"\n" +
                "  class=\"page-link\">" + i + "</button>\n" +
                "  </li>"
        } else {
            li += "<li class=\"paginate_button page-item\"><button value='" + i + "' onclick='getActionsByCollectionPoint(this.value)' " +
                "  aria-controls=\"dataTable\"\n" +
                "  data-dt-idx=" + i + "\n" +
                " tabindex=\"0\"\n" +
                "  class=\"page-link\">" + i + "</button>\n" +
                "  </li>"
        }
    }

    if (pageNumber === totalPages) {
        li += "<li class=\"paginate_button page-item next active\" id=\"dataTable_nextAction\"><button disabled \n" +
            "  href=\"#\" aria-controls=\"dataTable\" data-dt-idx=\"7\" tabindex=\"0\"\n" +
            "  class=\"page-link\">Next</button>\n" +
            " </li>"
    }
    if (pageNumber < totalPages) {
        li += "<li class=\"paginate_button page-item next \" id=\"dataTable_nextAction\"><button value='" + 1 + "' onclick='getActionsByCollectionPoint(PAGENUM+1)' " +
            "  href=\"#\" aria-controls=\"dataTable\" data-dt-idx=\"7\" tabindex=\"0\"\n" +
            "  class=\"page-link\">Next</button>\n" +
            " </li>"
    }

    return li;
}

function createViewDataTableLengthSelect(totalElements) {
    let selectOption = "";
    if (totalElements < 25) {
        selectOption += "<option value=10>10</option>\n"
    } else if (totalElements >= 25 && totalElements < 50) {
        selectOption += "<option value=10>10</option>\n" +
            " <option value=25>25</option>\n"
    } else if (totalElements >= 50 && totalElements < 100) {
        selectOption += "<option value=10>10</option>\n" +
            " <option value=25>25</option>\n" +
            " <option value=50>50</option>\n"
    } else if (totalElements >= 100) {
        selectOption += "<option value=10>10</option>\n" +
            " <option value=25>25</option>\n" +
            " <option value=50>50</option>\n" +
            " <option value=100>100</option>"
    }
    document.getElementById('dataTableLengthSelect').getElementsByTagName('option')[PAGESIZE].selected = 'selected';

    return selectOption;
}

function handleDataTableLengthSelect(pageSize) {
    // console.log(pageSize)
    PAGESIZE = parseInt(pageSize)
    getActionsByCollectionPoint(1, PAGESIZE);
}