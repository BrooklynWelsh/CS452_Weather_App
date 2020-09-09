-- Code for dropping tables instead of the entire DB, kept just in case needed
DO $$ DECLARE
    r RECORD;
BEGIN
    -- This loop removes all tables in the current DB
    -- if the schema you operate on is not "current", you will want to
    -- replace current_schema() in query with 'schematodeletetablesfrom'
    -- *and* update the generate 'DROP...' accordingly.
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = current_schema()) LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
    END LOOP;
END $$;

--DROP DATABASE IF EXISTS weather_app;
--CREATE DATABASE weather_app WITH ENCODING UTF8 OWNER pi;
--SELECT DATABASE weather_app;

CREATE TABLE IF NOT EXISTS stations (
	stationID SERIAL,
	name varchar(100),
	latitude float NOT NULL,
	longitude float NOT NULL,
	altitude float NOT NULL,
	CONSTRAINT stations_pk PRIMARY KEY (stationID),
	CONSTRAINT unique_attributes UNIQUE (name, latitude, longitude)
);

CREATE TABLE IF NOT EXISTS measurements_current (
	stationID int,
	fahrenheit real,
	celsius real,
	wind_speed real,
	wind_gust real,
	pressure real,
	humidity real,
	rain real,
	cloud varchar(3),
	heat_index real,
	CONSTRAINT current_pk PRIMARY KEY (stationID)
);

CREATE TABLE IF NOT EXISTS measurements_historical (
        measurement_date  date,
	stationID int,
	fahrenheit_high real,
	fahrenheit_low real,
	celsius_high real,
	celsius_low real,
	wind_speed real,
	wind_gust real,
	pressure real,
	humidity real,
	rain real,
	cloud varchar(3),
	heat_index real,
	CONSTRAINT historical_pk PRIMARY KEY (measurement_date, stationID)
);

CREATE TABLE IF NOT EXISTS forecast_hourly (
	hour time,
	stationID int,
	fahrenheit real,
	celsius real,
	wind_speed real,
	wind_gust real,
	pressure real,
	humidity real,
	rain_probability real,
	cloud varchar(3),
	heatindex real,
	CONSTRAINT forecast_hourly_pk PRIMARY KEY (hour, stationID)
);

CREATE TABLE IF NOT EXISTS forecast_daily (
	day date,
	stationID int,
	fahrenheit_high real,
	fahrenheit_low real,
	celsius_high real,
	celsius_low real,
	wind_speed real,
	wind_gust real,
	pressure real,
	humidity real,
	rain_probability real,
	cloud varchar(3),
	heatindex real,
	CONSTRAINT forecast_daily_pk PRIMARY KEY (day, stationID)
);
