let collectionPointsList = [];
let uppgsLists = [];
let opcServersList = [];
let miningSystemId = 1;
let uppgId = 1

let opcId;

function handleOpcId() {
    opcId = document.getElementById('inputGroupSelect03').value
}

function getAllMiningSystems() {
    localStorage.removeItem("isAction")
    axios.get("/api/mining_system/all")
        .then(function (response) {
            if (response.data.message === "OK") {
                document.getElementById("miningSelect").innerHTML = createViewMiningOrUppgSelect(response.data.object)
            }
        })
        .catch(function (error) {
            console.log(error)
        })
}

function getAllUppgs() {
    localStorage.removeItem("isAction")
    axios.get("/api/uppg/all/mining_system/" + miningSystemId)
        .then(function (response) {
            if (response.data.message === "OK") {
                document.getElementById("uppgSelect").innerHTML = createViewMiningOrUppgSelect(response.data.object)
                uppgId = document.getElementById('uppgSelect').value;
                uppgsLists = response.data.object
                getAllCollectionPoints()
            }
        })
        .catch(function (error) {
            console.log(error)
        })
}

function selectHandleMining() {
    miningSystemId = document.getElementById('miningSelect').value;
    getAllUppgs()
}

function selectHandleUppg() {
    uppgId = document.getElementById('uppgSelect').value;
    getAllCollectionPoints()
}

function getAllCollectionPoints() {
    localStorage.removeItem("isAction")
    if (uppgId == "" || uppgId == null) {
        document.getElementById("uppgSelect").innerHTML = "<option value=''>Нет УППГ</option>"
        document.getElementById("collectionPointTable").innerHTML = "<tr class=\"odd\">\n" +
            "                                                <td class=\"sorting_1\">-</td>\n" +
            "                                                <td>-</td>\n" +
            "                                                <td>-</td>\n" +
            "\n" +
            "                                            </tr>"

        alert("В этом месторождении нет УППГ и СП ")
    } else {
        axios.get("/api/admin/collection_point/all/" + uppgId)
            .then(function (response) {
                // console.log(response.data)
                if (response.data.message === "OK") {
                    collectionPointsList = response.data.object
                }
                document.getElementById("collectionPointTable").innerHTML = createViewTable(response.data.object)
            })
            .catch(function (error) {
                console.log(error.response)
            })
    }
}

function getAllOpcServers() {
    axios.get("/api/admin/opc_server/all")
        .then(function (response) {
            // console.log("response.data")
            // console.log(response.data)
            document.getElementById("inputGroupSelect03").innerHTML = addOptionOpcServers(response.data.object)
            opcServersList = response.data.object
        })
        .catch(function (error) {
            console.log(error)
        })
}

function addCollectionPointBtn() {
    document.getElementById('addOrEditCollectionPointH3').innerText = 'Добавить Сборный пункт'
    document.getElementById('addOrEditCollectionPointBtn').innerText = 'Добавить'
    let formField = document.getElementById('addOrEditCollectionPointForm')
    formField['uppg'].value = uppgId;
}

function resetAndCloseForm() {
    document.getElementById('closeFormBtn').click();
    document.getElementById('addOrEditCollectionPointForm').reset();
}

function addOrEditCollectionPoint(event) {
    event.preventDefault();
    const formData = new FormData(event.target);
    const data = {}
    formData.forEach((value, key) => (data[key] = value));
    data.uppg = {};
    data.opcServer = {};

    // console.log("data 1")
    console.log(data)

    let config = {
        method: '',
        url: '',
        data
    };

    if (data.id === "" || data.id == null) {

        config.method = 'post';
        config.url = '/api/admin/collection_point/add/' + uppgId + '/' + opcId;
    } else {

        config.method = 'put';
        config.url = '/api/admin/collection_point/edit/' + uppgId + '/' + opcId;
    }

    axios(config)
        .then(function (response) {
            getAllCollectionPoints();
            resetAndCloseForm();
            // console.log(response.data);
        })
        .catch(function (error) {
            console.log(error.response.data);
        });
}

function editCollectionPoint(id) {
    document.getElementById('addOrEditCollectionPointH3').innerText = 'Редактировать Сборный пункт'
    document.getElementById('addOrEditCollectionPointBtn').innerText = 'Редактировать'
    let editCollectionPoint = collectionPointsList.find(collectionPoint => collectionPoint.id == id)
    let formField = document.getElementById('addOrEditCollectionPointForm')

    formField['id'].value = editCollectionPoint.id;
    formField['name'].value = editCollectionPoint.name;
    formField['temperatureUnit'].value = editCollectionPoint.temperatureUnit;
    formField['pressureUnit'].value = editCollectionPoint.pressureUnit;
    formField['opcServer'].value = editCollectionPoint.opcServer.id;
    opcId = editCollectionPoint.opcServer.id;
}

function deleteCollectionPoint(id) {
    axios.delete("/api/collection_point/delete/" + id)
        .then(function (response) {
            // console.log(response.data)
            getAllCollectionPoints()
        })
        .catch(function (error) {
            console.log(error.response.data)
        })
}

function changeCollectionPointIsActive(checkboxElem) {
    let isActive = checkboxElem.checked;
    let id = checkboxElem.name;
    console.log("isActive")
    console.log(isActive)
    axios.get("/api/collection_point/active/" + id + "/" + isActive)
        .then(function (response) {
            console.log(response.data)
            // getAllCollectionPoints()
        })
        .catch(function (error) {
            console.log(error.response.data)
        })
}

function createViewTable(collectionPoints) {
    let out = "";
    collectionPoints.map(collectionPoint => {
        out += "<tr class=\"collectionPoint_table_row\">\n" +
            "<td class=\"sorting_1\">" + collectionPoint.id + "</td>\n" +
            "<td>" + collectionPoint.name + "</td>\n" +
            "<td>" + collectionPoint.temperatureUnit + "</td>\n" +
            "<td>" + collectionPoint.pressureUnit + "</td>\n" +
            // "<td hidden value='" + collectionPoint.uppgId + "'>" + collectionPoint.uppgId + "</td>\n" +
            "<td>" + collectionPoint.opcServer.name + "</td>\n" +
            "<td>" +
            "<button data-target=\"#exampleModalCenter\" data-toggle=\"modal\" class='btn btn-success mt-1' id='btn-edit-collectionPoint' value='" + collectionPoint.id + "' onclick='editCollectionPoint(this.value)'>Редактировать</button>\n" +
            "<button  class='btn btn-info ml-2 mt-1' id='btn-action-mining' value='" + collectionPoint.id + "' onclick='clickActionBtn(this.value)'>Действие</button>" +
            "<button class='btn btn-danger ml-2 mt-1 mr-2' id='btn-edit-collectionPoint' value='" + collectionPoint.id + "' onclick='deleteCollectionPoint(this.value)'>Удалить</button>"

           if (collectionPoint.activeE){
               out+= "<label class=\"switch\">\n" +
                   "  <input name='" + collectionPoint.id + "' onchange='changeCollectionPointIsActive(this)' id="+collectionPoint.id+collectionPoint.activeE+" type=\"checkbox\" checked>\n" +
                   "  <span class=\"slider round\"></span>\n" +
                   "</label>"+
                   // "<input id="+collectionPoint.id+collectionPoint.activeE+" onchange='changeCollectionPointIsActive(this)' name='" + collectionPoint.id + "' type=\"checkbox\" checked='" + collectionPoint.activeE + "'  data-toggle=\"toggle\" data-onstyle=\"warning\">" +

                   "</td>\n" +
                   "</tr>"
           }else {
               out+= "<label class=\"switch\">\n" +
                   "  <input name='" + collectionPoint.id + "' onchange='changeCollectionPointIsActive(this)' id="+collectionPoint.id+collectionPoint.activeE+" type=\"checkbox\">\n" +
                   "  <span class=\"slider round\"></span>\n" +
                   "</label>"+
                   // "<input id="+collectionPoint.id+collectionPoint.activeE+" onchange='changeCollectionPointIsActive(this)' name='" + collectionPoint.id + "' type=\"checkbox\" checked='" + collectionPoint.activeE + "'  data-toggle=\"toggle\" data-onstyle=\"warning\">" +

                   "</td>\n" +
                   "</tr>"
           }
    })
    return out;
}

function createViewMiningOrUppgSelect(miningsOrUppgs) {
    let out = "";
    miningsOrUppgs.map(item => {
        out += "<option value='" + item.id + "'>" + item.name + "</option>"
    })
    return out;
}

function addOptionOpcServers(servers) {
    let out = "<option value=''>Устройсто</option>";
    servers.map(item => {
        out += "<option value='" + item.id + "'>" + item.name + "</option>"
    })
    return out;
}