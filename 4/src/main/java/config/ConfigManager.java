package config;

import constants.ConfigConstants;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private int staleBookMonths;
    private boolean markOrdersCompletedOnStockAdd;

    public ConfigManager() {
        loadProperties();
    }

    private void loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(ConfigConstants.CONFIG_PATH)) {
            properties.load(input);
            this.staleBookMonths = Integer.parseInt(properties.getProperty
                    (ConfigConstants.STALE_BOOK_MONTHS_KEY, String.valueOf(ConfigConstants.DEFAULT_STALE_BOOK_MONTHS)));
            this.markOrdersCompletedOnStockAdd = Boolean.parseBoolean(properties.getProperty
                    (ConfigConstants.MARK_ORDERS_COMPLETED_KEY, String.valueOf(ConfigConstants.DEFAULT_MARK_ORDERS_COMPLETED)));
        } catch (IOException e) {
            this.staleBookMonths = ConfigConstants.DEFAULT_STALE_BOOK_MONTHS;
            this.markOrdersCompletedOnStockAdd = ConfigConstants.DEFAULT_MARK_ORDERS_COMPLETED;
            throw new RuntimeException(e);
        }
    }

    public int getStaleBookMonths() {
        return staleBookMonths;
    }

    public boolean isMarkOrdersCompletedOnStockAdd() {
        return markOrdersCompletedOnStockAdd;
    }
}
