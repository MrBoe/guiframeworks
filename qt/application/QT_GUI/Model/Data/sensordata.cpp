//#########################################################################################
// Projekt: Heart Rate 2 go
// Copyright: 2014
//#########################################################################################

/**
  * @file   sensordata.cpp
  * @author Patrick Mathias, Markus Nebel
  * @author responsible: Patrick Mathias
  * @date   16.12.2014 12:02:00 GMT
  *
  * @brief  Implementation file of SensorData class
  *
  */

#include "sensordata.h"

SensorData::SensorData(const QDateTime& aDate,
                       quint16 aHeartRate,
                       quint16 aStepLength,
                       quint64 aId) :   m_date(aDate),
                                        m_heartRate(aHeartRate),
                                        m_stepLength(aStepLength),
                                        m_id(aId)
{
    // C'tor
}


const QDateTime &SensorData::getDate() const
{
    return m_date;
}

quint16 SensorData::getHeartRate() const
{
    return m_heartRate;
}

quint16 SensorData::getStepLength() const
{
    return m_stepLength;
}

void SensorData::setDate(const QDateTime& aDate)
{
    m_date = aDate;
}

void SensorData::setHeartRate(quint16 aHeartRate)
{
    m_heartRate = aHeartRate;
}

void SensorData::setStepLength(quint16 aStepLength)
{
    m_stepLength = aStepLength;
}

quint64 SensorData::getId() const
{
    return m_id;
}
