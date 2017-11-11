package fi.tkgwf.ruuvi.utils;

import fi.tkgwf.ruuvi.bean.RuuviMeasurement;
import fi.tkgwf.ruuvi.config.Config;
import java.util.ArrayList;
import java.util.List;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

public class InfluxDBConverter {

    public static BatchPoints toLegacyInflux(RuuviMeasurement measurement) {
        List<Point> points = new ArrayList<>();
        createAndAddLegacyFormatPointIfNotNull(points, "temperature", measurement.temperature, null, null);
        createAndAddLegacyFormatPointIfNotNull(points, "humidity", measurement.relativeHumidity, null, null);
        createAndAddLegacyFormatPointIfNotNull(points, "pressure", measurement.pressure, null, null);
        createAndAddLegacyFormatPointIfNotNull(points, "acceleration", measurement.accelerationX, "axis", "x");
        createAndAddLegacyFormatPointIfNotNull(points, "acceleration", measurement.accelerationY, "axis", "y");
        createAndAddLegacyFormatPointIfNotNull(points, "acceleration", measurement.accelerationZ, "axis", "z");
        createAndAddLegacyFormatPointIfNotNull(points, "acceleration", measurement.accelerationTotal, "axis", "total");
        createAndAddLegacyFormatPointIfNotNull(points, "batteryVoltage", measurement.batteryVoltage, null, null);
        createAndAddLegacyFormatPointIfNotNull(points, "rssi", measurement.rssi, null, null);
        // The 'legacy format' using single-value measurements is terribly inefficient in terms of space used, these will be available in the "new format" using multi-value measurements
        // createAndAddLegacyFormatPointIfNotNull(points, "absoluteHumidity", measurement.absoluteHumidity, null, null);
        // createAndAddLegacyFormatPointIfNotNull(points, "dewPoint", measurement.dewPoint, null, null);
        // createAndAddLegacyFormatPointIfNotNull(points, "equilibriumVaporPressure", measurement.equilibriumVaporPressure, null, null);
        // createAndAddLegacyFormatPointIfNotNull(points, "airDensity", measurement.airDensity, null, null);
        return BatchPoints
                .database(Config.getInfluxDatabase())
                .tag("protocolVersion", String.valueOf(measurement.dataFormat))
                .tag("mac", measurement.mac)
                .points(points.toArray(new Point[points.size()]))
                .build();
    }

    private static void createAndAddLegacyFormatPointIfNotNull(List<Point> points, String measurement, Number value, String extraTagKey, String extraTagValue) {
        if (value != null) {
            Point.Builder p = Point.measurement(measurement).addField("value", value);
            if (extraTagValue != null) {
                p.tag(extraTagKey, extraTagValue);
            }
            points.add(p.build());
        }
    }
}
