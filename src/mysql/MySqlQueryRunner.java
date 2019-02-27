package mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface MySqlQueryRunner {
	void runRowInstructions(ResultSet rs) throws SQLException;
}
