//#########################################################################################
// Projekt: Heart Rate 2 go
// Copyright: 2014
//#########################################################################################

/**
  * @file   sensorcalcmodel.cpp
  * @author Patrick Mathias, Markus Nebel
  * @author responsible: Patrick Mathias
  * @date   15.12.2014 21:52:00 GMT
  *
  * @brief  Implementation file of SensorCalcModel class
  *
  */

#include "sensorcalcmodel.h"

SensorCalcModel::SensorCalcModel(SensorModel& aModel, QObject* aParent): QAbstractListModel(aParent),
                                                                         m_Model(aModel)
{
    // C'tor
}

void SensorCalcModel::setNewSensorCalcModel(const QList<CalcSensorData> &aSensorModel)
{
    beginResetModel();
    m_calcSensorList = aSensorModel;
    endResetModel();
}

void SensorCalcModel::updateCalcValues(const SensorModel& aModel)
{
    beginResetModel();
    if(0 == aModel.m_sensorList.size())
    {
        // Default values
        m_calcSensorList.clear();
        m_calcSensorList.append(CalcSensorData(tr("maximal heartrate:"), 0, ""));
        m_calcSensorList.append(CalcSensorData(tr("minimal heartrate:"), 0, ""));
        m_calcSensorList.append(CalcSensorData(tr("average heartrate:"), 0, ""));
    }
    else
    {
        // helper variables for calculation
        double tmpAverage = 0;
        double tmpMaximum = aModel.m_sensorList.at(0)->getHeartRate();
        double tmpMinimum = aModel.m_sensorList.at(0)->getHeartRate();

        for(int i=0; i< aModel.m_sensorList.size(); i++)
        {
            // calc average
            tmpAverage = tmpAverage + aModel.m_sensorList.at(i)->getHeartRate();

            // calc maximum value
            if(tmpMaximum < aModel.m_sensorList.at(i)->getHeartRate())
            {
                tmpMaximum = aModel.m_sensorList.at(i)->getHeartRate();
            }

            // calc minimum value
            if(tmpMinimum > aModel.m_sensorList.at(i)->getHeartRate())
            {
                tmpMinimum = aModel.m_sensorList.at(i)->getHeartRate();
            }
        }

        // update Values
        m_calcSensorList.clear();
        m_calcSensorList.append(CalcSensorData(tr("maximal heartrate:"), tmpMaximum, ""));
        m_calcSensorList.append(CalcSensorData(tr("minimal heartrate:"), tmpMinimum, ""));
        m_calcSensorList.append(CalcSensorData(tr("average heartrate:"), QString::number(tmpAverage / aModel.m_sensorList.size(),'f',1).toDouble(), ""));
    }
    endResetModel();
}
