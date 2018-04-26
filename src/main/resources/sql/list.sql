SELECT
	Player,
	Score,
	Duration,
	PlayerName,
	Time_Recorded
FROM Scores

ORDER BY Score DESC, Duration, Player, PlayerName

LIMIT ?, ?;
