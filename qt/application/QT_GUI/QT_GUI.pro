QT += qml quick sql

# needed because of QCustom Plot
 greaterThan(QT_MAJOR_VERSION, 4): QT += widgets printsupport

CONFIG += c++11

SOURCES +=  main.cpp \
            Thirdparty/qcustomplot.cpp \
            Model/Data/sensordata.cpp \
            Model/sensormodel.cpp \
            Diagram/customplotbarchart.cpp \
            Diagram/customplotlinechart.cpp \
            Connection/DataReceiver.cpp \
            Connection/BroadcastReceiver.cpp \
            Connection/TcpConnection.cpp \
            Connection/TcpServer.cpp \
            Model/Data/calcsensordata.cpp \
            Model/activesensorcalcmodel.cpp \
            Model/inactivesensorcalcmodel.cpp \
            Model/sensorcalcmodel.cpp \
            ImportExport/ImportExport.cpp \
            Model/selectionmodel.cpp \
            Controller/selectioncontroller.cpp \
            ImportExport/MeasureType.cpp \
            ImportExport/MoodType.cpp \
            Controller/initdiagramscontroller.cpp \
            Controller/filtercontroller.cpp \
            Controller/tableselectioncontroller.cpp \
            Controller/printcontroller.cpp \
    Settings/Settings.cpp

RESOURCES += qml.qrc \

HEADERS += \
            Model/Data/sensordata.h \
            Thirdparty/qcustomplot.h \
            RessourceFilePaths.h \
            Model/sensormodel.h \
            Diagram/customplotbarchart.h \
            Diagram/customplotlinechart.h \
            Connection/DataReceiver.h \
            Connection/BroadcastReceiver.h \
            Connection/TcpConnection.h \
            Connection/TcpServer.h \
            Model/Data/calcsensordata.h \
            Model/sensorcalcmodel.h \
            Model/inactivesensorcalcmodel.h \
            Model/activesensorcalcmodel.h \
            ImportExport/ImportExport.h \
            Model/selectionmodel.h \
            Controller/selectioncontroller.h \
            ImportExport/MeasureType.h \
            ImportExport/MoodType.h \
            Controller/initdiagramscontroller.h \
            Controller/filtercontroller.h \
            Controller/tableselectioncontroller.h \
            Controller/printcontroller.h \
    Settings/Settings.h

DESTPATH = .
target.path = $$DESTPATH

qml.files = *.qml
qml.path = $$DESTPATH/View

INSTALLS += target qml
