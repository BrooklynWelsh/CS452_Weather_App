-- Test data for weather DB

-- Weather station
INSERT INTO stations(name,latitude,longitude,altitude) VALUES('Lynchburg Regional Airport Station', 37.3261,79.2014,938);
INSERT INTO stations(name,latitude,longitude,altitude) VALUES('Montgomery Exec Station', 37.21, -80.41, 2132);

-- Historical data for station1
INSERT INTO measurements_historical(measurement_date,stationid,fahrenheit_high,fahrenheit_low,celsius_high,celsius_low,wind_speed,wind_gust,pressure,humidity,rain,cloud,heat_index) SELECT '2020-09-02',stationid,89,72,33,22,16,22,29.05,88,10,'CLD',99 FROM stations WHERE name = 'Lynchburg Regional Airport Station';

-- Forecast (daily & hourly) for station2
INSERT INTO forecast_hourly SELECT '12:00',stationid,73,23,7,6,30.20,89,13,'CLD',75 FROM stations WHERE name = 'Montgomery Exec Station';
INSERT INTO forecast_daily SELECT '2020-09-09',stationid,75,65,24,18,6,10,30.20,89,40,'SHW',75 FROM stations WHERE name = 'Montgomery Exec Station';

-- Current measurements for both
INSERT INTO measurements_current SELECT stationid,73,23,3,0,30.12,82,0,'CLR',73 FROM stations WHERE name = 'Lynchburg Regional Airport Station';
INSERT INTO measurements_current SELECT stationid,67,19,3,0,30.24,90,0,'FAR',67 FROM stations WHERE name = 'Montgomery Exec Station';
