//#########################################################################################
// Projekt: Heart Rate 2 go
// Copyright: 2014
//#########################################################################################

/**
  * @file   printcontroller.h
  * @author Patrick Mathias, Markus Nebel
  * @author responsible: Patrick Mathias
  * @date   20.12.2014 14:55:00 GMT
  *
  * @brief  Inlcude all declarations from PrintController
  *
  */

#ifndef PRINTCONTROLLER_H
#define PRINTCONTROLLER_H

#include <QObject>
#include <QDebug>
#include <QPrinter>
#include <QPrintDialog>
#include "Model/sensormodel.h"
#include <QTextDocument>

/**
 * @brief The PrintController class Print all selected inactive and active sensor data
 *
 *  This class use a QTextDocument to format print output. The current format is given through html
 *  code
 */
class PrintController : public QObject
{
    Q_OBJECT
    public:

        /**
         * @brief PrintController Constructor to init all attributes
         * @param aParent                   Pointer to qObject parent class
         * @param aModelForInactiveData     Reference to SensorModel with inactvie sensor data
         * @param aModelForActiveData       Reference to SensorModel with active sensor data
         */
        PrintController(QObject* aParent, SensorModel& aModelForInactiveData, SensorModel& aModelForActiveData);

        /**
         * @brief PrintButtonController Copy-Constructor is not allowed
         * @param aOther    Reference to a other PrintButtonController to init Object
         */
        PrintController(const PrintController& aOther) = delete;

        /**
         * @brief operator = Copy-Assigment Operator is not allowed
         * @param aRhs  Right side of Copy-Assigment Operator
         * @return
         */
        PrintController& operator= (const PrintController& aRhs) = delete;

    public slots:

        /**
         * @brief clickPrintButtonSlot Slot to print all data
         */
        void clickPrintButtonSlot();

    private:

        /**
         * @brief m_inactiveDatamodel SensorModel with the current inactvie sensor data
         */
        SensorModel& m_inactiveDatamodel;

        /**
         * @brief m_activeDataModel SensorModel with the current active sensor data
         */
        SensorModel& m_activeDataModel;

        /**
         * @brief createSensorDataFile Create a QTextDocument with all data in html format and delegate print to QPrinter
         * @param aPrinter          Reference to a QPrinter Object to print data
         * @param aModel            Reference to a SensorModel with the actual data
         * @param aOverviewName     Overview name for print output
         */
        void createSensorDataFile(QPrinter& aPrinter,const SensorModel& aModel, const QString aOverviewName);
};

#endif // PRINTBUTTONCONTROLLER_H
