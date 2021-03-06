//#########################################################################################
// Projekt: Heart Rate 2 go
// Copyright: 2014
//#########################################################################################

/**
  * @file   DataReceiver.cpp
  * @author Patrick Mathias, Markus Nebel
  * @author responsible: Markus Nebel
  * @date   12.12.2014 14:33:34 GMT
  *
  * @brief  Implementation file of DataReceiver class
  *
  */


#include "DataReceiver.h"
#include "Model/inactivesensorcalcmodel.h"
#include "Model/activesensorcalcmodel.h"

#include <QDebug>
#include <QList>
#include <QDateTime>
#include <QUrlQuery>
#include <QtEndian>

DataReceiver* DataReceiver::mInstance = 0;

DataReceiver::DataReceiver(QObject *parent) :
    QObject(parent)
{
}

DataReceiver& DataReceiver::getInstance()
{
    if (!mInstance)
    {
        mInstance = new DataReceiver();
    }

    return *mInstance;
}

bool DataReceiver::validateData(const quint8* aData, const quint64 aLen)
{
    quint64 pos = 0;
    quint8 mode, mood;
    quint16 averageRate;

    //QList<const SensorData*> sensorData;
    QList<rawData> rawDataList;
    STATEMACHINE state = MODE_STATE;

    do
    {
        // switch through datatypes:
        switch (aData[pos])
        {
            case MODE:
                // expect that we are in MODE_STATE
                if (MODE_STATE == state)
                {
                    qDebug("MODE data packet found");
                    mode = aData[pos+1];
                    pos += 2;
                    // switch to next state:
                    state = MOOD_STATE;
                }
                else
                {
                    qFatal("unexpected data packet MODE in state" + state);
                    return false;
                }
            break;

            case MOOD:
                // expect that we are in MOOD_STATE
                if (MOOD_STATE == state)
                {
                    qDebug("MOOD data packet found");
                    mood = aData[pos+1];
                    pos += 2;
                    // switch to next state:
                    state = AVGR_STATE;
                }
                else
                {
                    qFatal("unexpected data packet MOOD in state" + state);
                    return false;
                }
            break;

            case AVGR:
                // expect that we are in MOOD_STATE
                if (AVGR_STATE == state)
                {
                    qDebug("AVGR data packet found");
                    pos++;
                    memcpy(&averageRate, aData+pos, 2); pos += 2;
                    averageRate = qFromBigEndian(averageRate);

                    // switch to next state:
                    state = DATA_STATE;
                }
                else
                {
                    qFatal("unexpected data packet AVGR in state" + state);
                    return false;
                }
            break;

            case DATA:
                if (DATA_STATE == state)
                {
                    pos++;
                    qDebug("DATA data packet found: ");
                    if (12 <= (aLen - pos))
                    {
                        quint64 timeStampMs;
                        quint16 heartRate, steps;

                        memcpy(&timeStampMs, aData+pos, 8); pos += 8;
                        memcpy(&heartRate, aData+pos, 2); pos += 2;
                        memcpy(&steps    , aData+pos, 2); pos += 2;

                        // rewind network byte order:
                        timeStampMs = qFromBigEndian(timeStampMs);
                        heartRate = qFromBigEndian(heartRate);
                        steps = qFromBigEndian(steps);

                        rawDataList.append({timeStampMs, heartRate, steps});
                        /*QDateTime dateTime = QDateTime::fromMSecsSinceEpoch(timeStampMs);
                        sensorData.append(new SensorData(dateTime, heartRate, steps));*/
                    }
                    else if (0 == (aLen - pos))
                    {
                        // switch into final state:
                        state = FINAL_STATE;
                    }
                    else
                    {
                        qFatal("packet is too short for packet type DATA");
                        return false;
                    }
                }
                else
                {
                    qFatal("unexpected data packet DATA in state" + state);
                    return false;
                }
            break;

            default:
                qFatal("invalid datatype!");
            return false;
        }
    } while (pos < aLen);

    if (FINAL_STATE == state)
    {
        emit updateStorage(rawDataList, mode, mood, averageRate);
        emit signalizeController(mode);
    }

    return false;
}
