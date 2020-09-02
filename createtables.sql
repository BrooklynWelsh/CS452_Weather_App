
CREATE TABLE IF NOT EXISTS stations (
	stationID int,
	name varchar(100),
	latitude float NOT NULL,
	longitude float NOT NULL,
	altitude float NOT NULL,
	CONSTRAINT stations_pk PRIMARY KEY (stationID)
);

CREATE TABLE IF NOT EXISTS measurements_current (
	stationID int,
	fahrenheit real NOT NULL,
	celsius real  NOT NULL,
	wind_speed real NOT NULL,
	wind_gust real NOT NULL,
	pressure real NOT NULL,
	humidity real NOT NULL,
	rain real NOT NULL,
	cloud varchar(3) NOT NULL,
	heat_index real NOT NULL,
	CONSTRAINT current_pk PRIMARY KEY (stationID)
);

CREATE TABLE IF NOT EXISTS measurements_historical (
	time_stamp timestamp,
	stationID int,
	fahrenheit real NOT NULL,
	celsius real NOT NULL,
	wind_speed real NOT NULL,
	wind_gust real NOT NULL,
	pressure real NOT NULL,
	humidity real NOT NULL,
	rain real NOT NULL,
	cloud varchar(3) NOT NULL,
	heat_index real NOT NULL,
	CONSTRAINT historical_pk PRIMARY KEY (time_stamp, stationID)
);

CREATE TABLE IF NOT EXISTS measurements_forecast (
	time_stamp timestamp,
	stationID int,
	fahrenheit real NOT NULL,
	celsius real NOT NULL,
	wind_speed real NOT NULL,
	wind_gust real NOT NULL,
	pressure real NOT NULL,
	humidity real NOT NULL,
	rain real NOT NULL,
	cloud varchar(3) NOT NULL,
	heatindex real NOT NULL,
	CONSTRAINT forecast_pk PRIMARY KEY (time_stamp, stationID)
);
