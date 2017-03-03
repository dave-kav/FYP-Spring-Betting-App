package dk.cit.fyp.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dk.cit.fyp.domain.Horse;
import dk.cit.fyp.mapper.HorseRowMapper;

@Repository
public class JdbcHorseRepo implements HorseDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public JdbcHorseRepo(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Horse get(String name) {
		String sql = "SELECT * FROM Horses WHERE Name = ?";
		return jdbcTemplate.queryForObject(sql, new Object [] {name}, 
				new HorseRowMapper());
	}

	@Override
	public void save(Horse horse) {
		if ( get(horse.getName()) == null )
			update(horse);
		else
			add(horse);
	}
	
	private void add(Horse horse) {
		String sql = "INSERT INTO Horses (Name, Race_id, odds_numerator, odds_denominator) "
				+ "VALUES (?, ?, ?, ?)";
		
		jdbcTemplate.update(sql, new Object[] {horse.getName(), horse.getRaceID(), 
				horse.getOddsEnumerator(), horse.getOddsDenominator()});
	}
	
	private void update(Horse horse) {
		String sql = "UPDATE Horses SET Race_id = ?, odds_numerator = ?, odds_denominator = ?) "
				+ "WHERE Name = ?)";
		
		jdbcTemplate.update(sql, new Object[] {horse.getRaceID(), horse.getOddsEnumerator(), 
				horse.getOddsDenominator(), horse.getName()});
	}

	@Override
	public List<Horse> findAll() {
		String sql = "SELECT * FROM Horses";
		return jdbcTemplate.query(sql, new HorseRowMapper());
	}

	@Override
	public List<Horse> getHorsesInRace(int raceID) {
		String sql = "SELECT * FROM Horses WHERE Race_id = ?";		
		return jdbcTemplate.query(sql, new Object[] {raceID}, new HorseRowMapper());
	}

}
