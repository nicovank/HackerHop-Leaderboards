CREATE TABLE Scores (
	ID            INT                                 NOT NULL AUTO_INCREMENT,
	Player        ENUM ('ROB', 'NICK', 'KATIE', 'YE') NOT NULL,
	Score         BIGINT                              NOT NULL,
	Duration      BIGINT                              NOT NULL,
	PlayerName    VARCHAR(50)                         NOT NULL,
	Time_Recorded DATETIME                            NOT NULL,

	PRIMARY KEY (ID),
	UNIQUE (Player, Score, Duration, PlayerName)
);