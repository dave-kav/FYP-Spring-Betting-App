package dk.cit.fyp.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import dk.cit.fyp.domain.Race;
import dk.cit.fyp.mapper.RaceRowMapper;

@Repository
public class JdbcRaceRepo implements RaceDAO {

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public JdbcRaceRepo(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public Race get(int raceID) {
		String sql = "SELECT * FROM Races WHERE Race_id = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] {raceID}, new RaceRowMapper());
	}
	
	@Override
	public List<Race> find(String time) {
		String sql = "SELECT * FROM Races WHERE Time = ?";
		
		return jdbcTemplate.query(sql, new Object[] {time}, new RaceRowMapper());
	}

	@Override
	public void save(Race race) {
		if (race.getRaceID() != 0)
			update(race);
		else
			add(race);
	}
	
	private void add(Race race) {
		String sql = "INSERT INTO Races (Time, Date, Racetrack, Terms, Places, Runners) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date time = null;
		try {
			time = sdf.parse(race.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String formattedTime = sdf.format(time);
		
		jdbcTemplate.update(sql, new Object[] {formattedTime, race.getDate(), race.getTrack(), race.getTerms(),
											race.getPlaces(), race.getRunners()});
	}
	
	private void update(Race race) {
		String sql = "UPDATE Races SET Winner = ?, Place1 = ?, Place2 = ?, Place3 = ?, Settled = ? WHERE Race_id = ?";		
		
		int[] places = {0,0,0};
		if(race.getPlacedHorses()!= null && race.getPlacedHorses().size() > 0) {
			for (int i = 0; i < race.getPlacedHorses().size(); i++) {
				places[i] = race.getPlacedHorses().get(i).getSelectionID();
			}
		}
		
		int selectionID = 0;
		if (race.getWinner() != null)
			selectionID = race.getWinner().getSelectionID();
		
		jdbcTemplate.update(sql, new Object[] {selectionID, places[0], places[1], places[2], 
				race.isSettled(), race.getRaceID()});
	}
	

	@Override
	public List<Race> findAll() {
		String sql = "SELECT * FROM Races";
		return jdbcTemplate.query(sql, new RaceRowMapper());
	}

	@Override
	public List<String> getTracks() {
		String sql = "SELECT DISTINCT Racetrack FROM Races";
		
		return jdbcTemplate.query(sql, new RowMapper<String>(){

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		});
	}
	
	@Override
	public List<Race> getRaceTimes() {
		String sql = "SELECT * FROM Races ORDER BY Time";
		
		return jdbcTemplate.query(sql, new RaceRowMapper());

	}

	@Override
	public List<String> getTimesByTrack(String track) {
		String sql = "SELECT DISTINCT Time FROM Races WHERE Racetrack = ? ORDER BY Time";

		return jdbcTemplate.query(sql, new RowMapper<String>(){

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString(1);
			}
		}, new Object[]{track});
	}
}
