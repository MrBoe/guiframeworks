package de.masterios.heartrate2go.common;

import android.content.Context;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeartRateMeasure {

    private static final String LINE_SEPARATOR = "\n";
    private static final int TCP_DATA_PORT = 1234;

    //private static HeartRateMeasure singleton;

    private MeasureMode mMeasureMode;
    private MeasureMood mMeasureMood;
    private short mAverageHeartRate;
    private List<HeartRateData> mHeartRateDataList;

    // Private Contructor for previous using of singleton pattern
    // Is now bypassed with getInstance Method returning a new Object each time
    private HeartRateMeasure() {
        if(null == mHeartRateDataList) {
            mHeartRateDataList = new ArrayList<>();
        } else {
            mHeartRateDataList.clear();
        }

        mMeasureMode = MeasureMode.ACTIVITY;
        mMeasureMood = MeasureMood.AVERAGE;
    }

    /**
     * Get measure mode
     * @return
     */
    public synchronized MeasureMode getMeasureMode() {
        return mMeasureMode;
    }

    /**
     * Set measure mode: ACTIVITY, REST
     * @param measureMode
     */
    public synchronized void setMeasureMode(MeasureMode measureMode) {
        mMeasureMode = measureMode;
    }

    /**
     * Get measure mood
     * @return
     */
    public synchronized MeasureMood getMeasureMood() {
        return mMeasureMood;
    }

    /**
     * Set measure mood: GOOD, BAD, AVERAGE
     * @param measureMood
     */
    public synchronized void setMeasureMood(MeasureMood measureMood) {
        mMeasureMood = measureMood;
    }

    /**
     * Returning new HeartRateMeasure Object each time. No singleton anymore.
     * @return
     */
    public synchronized static HeartRateMeasure getInstance() {
        /*
        if(null == singleton) {
            singleton = new HeartRateMeasure();
        }
        return singleton;
        */

        return new HeartRateMeasure();
    }

    /**
     * Refresh average heartrate field from current datasets
     */
    private void refreshAverageHeartRate() {
        int sumOfHeartRates = 0;
        int counter = 0;
        for(HeartRateData heartRateData : mHeartRateDataList) {
            int heartRate = heartRateData.getHeartRate();
            if(0 != heartRate) {
                sumOfHeartRates += heartRate;
                counter++;
            }
        }
        mAverageHeartRate = (short)((double)sumOfHeartRates / (double) counter);
    }

    /**
     * Threadsafe adding of HeartRateData sets to list with refreshing of average value
     * @param heartRateData
     */
    public synchronized void add(HeartRateData heartRateData) {
        if(!mHeartRateDataList.contains(heartRateData)) {
            mHeartRateDataList.add(heartRateData);
            refreshAverageHeartRate();
        }
    }

    /**
     * Threadsafe removing of HeartRateData sets from list with refreshing of average value
     * @param heartRateData
     */
    public synchronized void remove(HeartRateData heartRateData) {
        if(mHeartRateDataList.contains(heartRateData)) {
            mHeartRateDataList.remove(heartRateData);
            refreshAverageHeartRate();
        }
    }

    /**
     * Threadsafe clearing of list with refreshing of average value
     */
    public synchronized void clear() {
        if(mHeartRateDataList.size() > 0) {
            mHeartRateDataList.clear();
            refreshAverageHeartRate();
        }
    }

    /**
     * Returning size of list
     * @return
     */
    public int getSize() {
        return mHeartRateDataList.size();
    }

    /**
     * Get heartrate value (in BPM) from specified dataset with provided index
     * @param index
     * @return
     */
    public int getHeartRateAt(int index) {
        int heartRate = 0;
        if(index >= 0 && index < mHeartRateDataList.size()) {
            heartRate =  mHeartRateDataList.get(index).getHeartRate();
        }
        return heartRate;
    }

    /**
     * Get TimeStamp value (in milliseconds) from specified dataset with provided index
     * @param index
     * @return
     */
    public long getTimeStampMsAt(int index) {
        long timestampMs = 0;
        if(index >= 0 && index < mHeartRateDataList.size()) {
            timestampMs =  mHeartRateDataList.get(index).getTimeStampMs();
        }
        return timestampMs;
    }

    /**
     * Get TimeStamp value (in milliseconds) from first dataset
     * @return
     */
    public long getStartTimeStampMs() {
        long timeSpampMs = -1;
        if(mHeartRateDataList.size() > 0) {
            timeSpampMs = mHeartRateDataList.get(0).getTimeStampMs();
        }
        return timeSpampMs;
    }

    /**
     * Calculate Difference (in milliseconds) between first and last dataset
     * @return
     */
    public long getDurationMs() {
        long duration = 0;
        if(MeasureMode.REST == mMeasureMode) {
            duration = 60000;
        } else {
            int size = mHeartRateDataList.size();
            if(size > 0) {
                long start = mHeartRateDataList.get(0).getTimeStampMs();
                long end = mHeartRateDataList.get(size-1).getTimeStampMs();
                duration = end - start;
            }
        }
        return duration;
    }

    /**
     * Get Comma-separated-value string of all data in current HeartRateMeasure object
     * Style:
     * measure_mode;measure_mood;average_value
     * timestamp_ms;heartrate;amount_steps
     * timestamp_ms;heartrate;amount_steps
     * [...]
     * @return
     */
    public String getDataAsString() {
        StringBuilder sb = new StringBuilder();
        if(mHeartRateDataList.size() > 0) {
            // begin settings
            sb.append("measure_mode=" + mMeasureMode.toString() + ";");
            sb.append("measure_mood=" + mMeasureMood.toString() + ";");
            sb.append("average_heart_rate=" + mAverageHeartRate);
            sb.append(LINE_SEPARATOR);
            // end settings
            HeartRateData lastDataSet = mHeartRateDataList.get(mHeartRateDataList.size() - 1);
            for (HeartRateData heartRateData : mHeartRateDataList) {
                sb.append(heartRateData.getTimeStampMs() + ";");
                sb.append(heartRateData.getHeartRate() + ";");
                sb.append(heartRateData.getSteps());

                if (heartRateData != lastDataSet) {
                    sb.append(LINE_SEPARATOR);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Set data in current HeartRateMeasure object from comma-separated-value string
     * Style:
     * measure_mode;measure_mood;average_value
     * timestamp_ms;heartrate;amount_steps
     * timestamp_ms;heartrate;amount_steps
     * [...]
     * @param data
     */
    public synchronized void setDataFromString(String data) {
        System.out.println(data);
        if(null != data && !data.equals("")) {
            String[] lines = data.split(LINE_SEPARATOR);
            // begin settings
            String[] settings = lines[0].split(";");
            for(String setting : settings) {
                if(setting.contains("=")) {
                    String[] pair = setting.split("=");
                    if(2 == pair.length) {
                        String key = pair[0];
                        String value = pair[1];

                        if(key.equals("measure_mode")) {
                            try { mMeasureMode = MeasureMode.valueOf(value); } catch (IllegalArgumentException e) { e.printStackTrace(); }
                        } else if(key.equals("measure_mood")) {
                            try { mMeasureMood = MeasureMood.valueOf(value); } catch (IllegalArgumentException e) { e.printStackTrace(); }
                        } else if(key.equals("average_heart_rate")) {
                            try { mAverageHeartRate = Short.parseShort(value); } catch (NumberFormatException e) { e.printStackTrace(); }
                        }
                    }
                }
            }
            // end settings

            clear();
            for(int i=1; i < lines.length; i++) {
                String[] dataset = lines[i].split(";");
                if(3 == dataset.length) {
                    try {
                        long timestamp = Long.parseLong(dataset[0]);
                        int heartrate = Integer.parseInt(dataset[1]);
                        int steps = Integer.parseInt(dataset[2]);
                        add(new HeartRateData(timestamp, heartrate, steps));
                    } catch (NumberFormatException e) { e.printStackTrace(); }
                }
            }
        }
    }

    DataSentListener mDataSentListener;
    public interface DataSentListener {
        public void onDataSent(boolean success);
    }

    /**
     * Set Listener callback for data-sent-event from Method "sendDataAsync"
     * @param dataSentListener
     */
    public void setDataSentListener(DataSentListener dataSentListener) {
        mDataSentListener = dataSentListener;
    }

    /**
     * Send data asynchronous to specified ip-address
     * Data will be sent with Prefix-Byte-Codes provided in "ByteCodes" class.
     * @param ip
     */
    public synchronized void sendDataAsync(final String ip) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = false;
                try {
                    Socket socket = new Socket(ip, TCP_DATA_PORT);
                    OutputStream out = socket.getOutputStream();
                    DataOutputStream outputStream = new DataOutputStream(out);
                    if (mHeartRateDataList.size() > 0) {
                        outputStream.write(ByteCodes.MEASURE_MODE);
                        switch (mMeasureMode) {
                            case ACTIVITY:
                                outputStream.write(ByteCodes.MEASURE_MODE_ACTIVITY);
                                break;
                            case REST:
                                outputStream.write(ByteCodes.MEASURE_MODE_REST);
                                break;
                        }

                        outputStream.write(ByteCodes.MEASURE_MOOD);
                        switch (mMeasureMood) {
                            case GOOD:
                                outputStream.write(ByteCodes.MEASURE_MOOD_GOOD);
                                break;
                            case AVERAGE:
                                outputStream.write(ByteCodes.MEASURE_MOOD_AVERAGE);
                                break;
                            case BAD:
                                outputStream.write(ByteCodes.MEASURE_MOOD_BAD);
                                break;
                        }

                        outputStream.write(ByteCodes.MEASURE_AVERAGE_HEART_RATE);
                        outputStream.write(ByteBuffer.allocate(2).putShort((short) mAverageHeartRate).array());

                        outputStream.write(ByteCodes.DATASET);
                        for (HeartRateData heartRateData : mHeartRateDataList) {
                            outputStream.write(ByteBuffer.allocate(8).putLong(heartRateData.getTimeStampMs()).array());
                            outputStream.write(ByteBuffer.allocate(2).putShort((short) heartRateData.getHeartRate()).array());
                            outputStream.write(ByteBuffer.allocate(2).putShort((short) heartRateData.getSteps()).array());
                            outputStream.write(ByteCodes.DATASET);
                        }
                    }
                    outputStream.close();
                    socket.close();
                    isSuccess = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(null != mDataSentListener) {
                    mDataSentListener.onDataSent(isSuccess);
                }
            }
        }).start();
    }

    /**
     * Calculates median value of all datasets and replaces
     * all datasets with a single dataset with this value.
     */
    public void convertToRestMeasurement() {
        mMeasureMode = MeasureMode.REST;
        if(mHeartRateDataList.size() > 0) {
            short[] field = new short[256];
            for (int i = 0; i < field.length; i++) {
                field[i] = 0;
            }

            for (HeartRateData heartRateData : mHeartRateDataList) {
                int heartrate = heartRateData.getHeartRate();
                if (0 < heartrate && heartrate < 256) {
                    field[heartrate]++;
                }
            }

            int middleValue = 0;
            int maxCount = 0;
            for (int i = 0; i < field.length; i++) {
                if (field[i] > maxCount) {
                    maxCount = field[i];
                    middleValue = i;
                }
            }

            HeartRateData first = mHeartRateDataList.get(0);
            mHeartRateDataList.clear();
            mHeartRateDataList.add(new HeartRateData(first.getTimeStampMs(), middleValue, 0));
        }
    }

    /**
     * Fills current HeartRateMeasure object with random data (for testing)
     */
    public void createRandomTestData() {
        final long tenYears = 315360000000L;
        final long minValue = System.currentTimeMillis() - tenYears;
        Random rdm = new Random(minValue);
        long currentTime = minValue + (((long)rdm.nextInt(315360000)) * 1000L);

        int mode = rdm.nextInt(3);
        switch(mode) {
            case 0: mMeasureMode = MeasureMode.ACTIVITY; break;
            case 1: mMeasureMode = MeasureMode.ACTIVITY; break;
            case 2: mMeasureMode = MeasureMode.REST; break;
        }

        int mood = rdm.nextInt(3);
        switch(mood) {
            case 0: mMeasureMood = MeasureMood.GOOD; break;
            case 1: mMeasureMood = MeasureMood.AVERAGE; break;
            case 2: mMeasureMood = MeasureMood.BAD; break;
        }

        clear();

        int amountOfDatasets = 1;
        if(MeasureMode.ACTIVITY == mMeasureMode) {
            amountOfDatasets += rdm.nextInt(99);
        }

        int steps = 0;
        for(int i=0; i < amountOfDatasets; i++) {
            long time = currentTime+i*3000;
            int heartrate = 50 + rdm.nextInt(100);
            steps += rdm.nextInt(10);
            add(new HeartRateData(time, heartrate, steps));
        }
    }
}
