package nye.adatbazis;
import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:game_highscores.db";

    // Adatbázis kapcsolat létrehozása
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
        // Kapcsolódik az SQLite adatbázishoz.
    }

    // Játékosok táblájának létrehozása, ha nem létezik
    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS players ("
                + "name TEXT PRIMARY KEY," //A játékos neve elsődleges kulcs
                + "wins INTEGER NOT NULL DEFAULT 0" //Győzelmek száma alapértelmezett értékkel
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            // A játékosok tábla létrehozása, ha még nem létezik.
        } catch (SQLException e) {
            System.out.println("Database initialization error: " + e.getMessage());
            // Hibaüzenet kiírása adatbázis inicializálás során.

        }
    }

    // Játékos nyereményének frissítése
    public static void updatePlayerWins(String playerName) {
        String updateSQL = "INSERT INTO players (name, wins) VALUES (?, 1) "
                + "ON CONFLICT(name) DO UPDATE SET wins = wins + 1;";
        // Ha a játékos még nem létezik, akkor létrehozza, egyébként növeli a győzelmek számát.


        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, playerName); // A lekérdezéshez hozzáadja a játékos nevét.
            pstmt.executeUpdate();  // Lekérdezés végrehajtása.
        } catch (SQLException e) {
            System.out.println("Error updating player wins: " + e.getMessage());
            // Hibaüzenet, ha a frissítés sikertelen.

        }
    }

    // High score táblázat megjelenítése
    public static void displayHighScores() {
        String selectSQL = "SELECT name, wins FROM players ORDER BY wins DESC LIMIT 10;";
        // Kiválasztja a játékosokat a győzelmeik szerint csökkenő sorrendben (10 legjobb).

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(selectSQL)) {
            System.out.println("High Scores:");
            while (rs.next()) {
                String name = rs.getString("name"); //Játékos neve.
                int wins = rs.getInt("wins"); //Játékos győzelmeinek száma.
                System.out.println(name + ": " + wins);
                //High score tábla soainak kiírása.
            }
        } catch (SQLException e) {
            System.out.println("Error displaying high scores: " + e.getMessage());
        }
    }
}

