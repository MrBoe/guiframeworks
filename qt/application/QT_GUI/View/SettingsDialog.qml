import QtQuick 2.3
import QtQuick.Controls 1.2
import QtQuick.Dialogs 1.2

Dialog {
    id: settingsDialog
    visible: true
    title: qsTr("Settings")
    standardButtons: StandardButton.Save | StandardButton.Cancel

    contentItem: Rectangle {
        implicitWidth: 800
        implicitHeight: 600

        GroupBox {
            id: grpDevices

            anchors.top: parent.top
            anchors.topMargin: 5
            anchors.left: parent.left
            anchors.leftMargin: 5
            anchors.rightMargin: 5
            anchors.bottomMargin: 5

            width: parent.width - 10
            title: qsTr("Known network devices:")

            TableView {
                anchors.fill: parent

                id: tblDevices
                TableViewColumn
                {
                    role: "address";
                    title: qsTr("IP-Address");
                }

                TableViewColumn
                {
                    role: "lastConnection";
                    title: qsTr("Last connection");
                }

                TableViewColumn
                {
                    role: "uuId";
                    title: qsTr("UUID");
                }
            }
        }

        GroupBox {
            id: grpOthers

            anchors.top: grpDevices.bottom
            anchors.topMargin: 5
            anchors.left: parent.left
            anchors.leftMargin: 5
            anchors.rightMargin: 5
            anchors.bottomMargin: 5

            width: parent.width - 10
            title: qsTr("Miscellaneous:")

            Text {
                id: lblDateTimeFormat
                text: qsTr("Date-Time Mode:")
            }

            ComboBox {
                anchors.left: lblDateTimeFormat.right
                anchors.leftMargin: 5
                currentIndex: 0
                model: ListModel {
                    id: cbItems
                    ListElement { text: "DD-MM-YYYY HH:MM:SS"; }
                    ListElement { text: "DD.MM.YYYY HH:MM:SS"; }
                    ListElement { text: "DD MONTH YYYY HH:MM:SS"; }
                    ListElement { text: "MM-DD-YYYY HH:MM:SS"; }
                }
                width: 200
                onCurrentIndexChanged: console.debug(cbItems.get(currentIndex).text + ", " + currentIndex)
            }
        }

        Button {
            id: closeButton
            anchors.bottom: parent.bottom;
            anchors.bottomMargin: 5
            anchors.right: parent.right;
            anchors.rightMargin: 5

            text: "Close"
            onClicked: settingsDialog.click(StandardButton.Cancel)
        }

        Button {
            id: saveButton
            anchors.bottom: parent.bottom;
            anchors.bottomMargin: 5
            anchors.right: closeButton.left;
            anchors.rightMargin: 5

            text: "Save"
            onClicked: settingsDialog.click(StandardButton.Save)
        }
    }
}
