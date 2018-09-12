package com.rocket.rocketbot.accountSync;

import com.rocket.rocketbot.RocketBot;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

import java.sql.*;

@Getter
public class DbSQL {

    private RocketBot rocketBot;
    private final String tableName = "sync_data";
    private Connection connection;
    private String host, database, username, password;
    private int port;
    private Statement statement;

    public DbSQL(RocketBot rocketBot, String host, int port, String database, String username, String password) {
        this.rocketBot = rocketBot;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        try {
            openConnection();
            statement = connection.createStatement();
            /*
            CREATE TABLE IF NOT EXISTS `sample2` ( `name` TEXT NOT NULL , `d_id` TEXT NOT NULL , `d_user` TEXT NOT NULL , `mc_group` TEXT NOT NULL , `rewarded` BOOLEAN NOT NULL )
            CREATE TABLE IF NOT EXISTS `sample` ( `name` TEXT NOT NULL , `id` INT NOT NULL )
            statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "name VARCHAR NOT NULL, " +
                    "d_id VARCHAR NOT NULL, " +
                    "d_user VARCHAR NOT NULL, " +
                    "mc_group VARCHAR NOT NULL, " +
                    "rewarded BOOLEAN NOT NULL, " +
                    "PRIMARY KEY(name));");
             */
            String sql = String.format("CREATE TABLE IF NOT EXISTS `%s` ( " +
                    "`name` TEXT NOT NULL , " +
                    "`d_id` TEXT NOT NULL , " +
                    "`d_user` TEXT NOT NULL , " +
                    "`mc_group` TEXT NOT NULL , " +
                    "`rewarded` BOOLEAN NOT NULL )", tableName);
            statement.execute(sql);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
            rocketBot.getLogger().info("- Successfully connected to database!");
        }
    }

    public void closeConnection() {
        try {
            if(connection != null && connection.isClosed()) {
                connection.createStatement();
                rocketBot.getLogger().info("- Database connection closed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //INSERT INTO sync_data (name, d_id, d_user, mc_group, rewarded) VALUES ('test', 'test', 'test', 'test', true);
    public void insert(String name, String id, String d_user, String mc_group, boolean b) {
        String values = String.format("('%s', '%s','%s', '%s', %s)", name, id, d_user, mc_group, b);
        try {
            statement.execute("INSERT INTO " + tableName + " (name, d_id, d_user, mc_group, rewarded) VALUES " + values + ";");
        } catch (SQLException e) {
            String err = String.format("Could not insert data : %s", values);
            rocketBot.getLogger().severe(err);
        }
    }

    public void insertNew(String name) {
        insert(name, "Not Synced Yet", "Not Synced Yet", "Not Synced Yet", false);
    }

    private ResultSet getRow(String target, String argument) {
        try {
            return statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + target + " = '" + argument + "'");
        } catch (SQLException e) {
            return null;
        }
    }

    public ResultSet getRowByDID(String id) {
        return getRow("d_id", id);
    }

    public ResultSet getRowByName(String name) {
        return getRow("name", name);
    }

    public void sync(String name, String d_id, String d_user) {
        ProxyServer.getInstance().getScheduler().runAsync(RocketBot.getInstance(), () -> {
            try {
                //statement.execute("UPDATE sync_data SET d_id = '123123', d_user = 'username' WHERE name = 'CyRien_'");
                statement.execute("UPDATE sync_data SET d_id = '" + d_id  + "', d_user = '" + d_user +"' WHERE name = '" + name + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateGroup(String name, String value) {
        ProxyServer.getInstance().getScheduler().runAsync(RocketBot.getInstance(), () -> {
            try {
                statement.execute("UPDATE sync_data SET mc_group = '" + value + "' WHERE name = '" + name + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public void set(String name, String column, String value) {
        ProxyServer.getInstance().getScheduler().runAsync(RocketBot.getInstance(), () -> {
            try {
                statement.execute("UPDATE sync_data " +
                        "SET " + column + " = " + value +
                        " WHERE name = '" + name + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setBool(String name_where, String column, boolean value) {
        ProxyServer.getInstance().getScheduler().runAsync(RocketBot.getInstance(), () -> {
            try {
                statement.execute("UPDATE sync_data " +
                        "SET " + column + " = " + value +
                        " WHERE name = '" + name_where + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void reset(String name) {
        ProxyServer.getInstance().getScheduler().runAsync(RocketBot.getInstance(), () -> {
            try {
                statement.execute("UPDATE sync_data " +
                        "SET d_id = 'Not Synced Yet', d_user = 'Not Synced Yet', mc_group = 'Not Synced Yet' " +
                        "WHERE name = '" + name + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
