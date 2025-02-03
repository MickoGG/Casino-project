DROP DATABASE IF EXISTS `casino_project_db`;
CREATE DATABASE `casino_project_db`;
USE `casino_project_db`;


CREATE TABLE  Privilege 
( 
	 IDPri               integer   AUTO_INCREMENT  NOT NULL ,
	 Name                varchar(50)  NOT NULL ,
     CONSTRAINT  XPKPrivilege  PRIMARY KEY  CLUSTERED ( IDPri  ASC) ,
     CONSTRAINT  XAK1Privilege  UNIQUE ( Name   ASC)
);


CREATE TABLE  User 
( 
	 IDUser              integer   AUTO_INCREMENT  NOT NULL ,
	 IDPri               integer  NOT NULL ,
	 FullName            varchar(100)  NOT NULL ,
	 Email               varchar(100)  NOT NULL ,
	 Username            varchar(50)  NOT NULL ,
	 Password            varchar(50)  NOT NULL ,
	 RegistrationDate    datetime  NOT NULL ,
	 Balance             decimal(13,5)  NOT NULL ,
     CONSTRAINT  XPKUser  PRIMARY KEY  CLUSTERED ( IDUser  ASC) ,
     CONSTRAINT  XAK1User  UNIQUE ( Email   ASC) ,
     CONSTRAINT  XAK2User  UNIQUE ( Username   ASC) ,
     CONSTRAINT  R_1  FOREIGN KEY ( IDPri ) REFERENCES  Privilege ( IDPri ) ON DELETE NO ACTION ON UPDATE CASCADE
);


CREATE TABLE  Deposit 
( 
	 IDUser              integer  NOT NULL ,
	 IDDep               integer   AUTO_INCREMENT  NOT NULL ,
	 Date                datetime  NOT NULL ,
	 Amount              decimal(13,5)  NOT NULL ,
     CONSTRAINT  XPKDeposit  PRIMARY KEY  CLUSTERED ( IDDep  ASC) ,
     CONSTRAINT  R_4  FOREIGN KEY ( IDUser ) REFERENCES  User ( IDUser ) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE  Withdraw 
( 
	 IDUser              integer  NOT NULL ,
	 IDWit               integer   AUTO_INCREMENT  NOT NULL ,
	 Date                datetime  NOT NULL ,
	 Amount              decimal(13,5)  NOT NULL ,
     CONSTRAINT  XPKWithdraw  PRIMARY KEY  CLUSTERED ( IDWit  ASC) ,
     CONSTRAINT  R_5  FOREIGN KEY ( IDUser ) REFERENCES  User ( IDUser ) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE  Game 
( 
	 IDGame              integer   AUTO_INCREMENT  NOT NULL ,
	 Name                varchar(50)  NOT NULL ,
     CONSTRAINT  XPKGame  PRIMARY KEY  CLUSTERED ( IDGame  ASC) ,
     CONSTRAINT  XAK1Game  UNIQUE ( Name   ASC)
);


CREATE TABLE  CurrentlyPlaying 
( 
	 IDUser              integer  NOT NULL ,
	 IDGame              integer  NOT NULL ,
	 StartTime           datetime  NOT NULL ,
	 TotalWager          decimal(13,5)  NOT NULL ,
	 TotalReceived       decimal(13,5)  NOT NULL ,
     CONSTRAINT  XPKCurrentlyPlaying  PRIMARY KEY  CLUSTERED ( IDUser  ASC, IDGame  ASC) ,
     CONSTRAINT  R_2  FOREIGN KEY ( IDUser ) REFERENCES  User ( IDUser ) ON DELETE CASCADE ON UPDATE CASCADE ,
     CONSTRAINT  R_3  FOREIGN KEY ( IDGame ) REFERENCES  Game ( IDGame ) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE  Session 
( 
	 IDSes               integer   AUTO_INCREMENT  NOT NULL ,
	 StartTime           datetime  NOT NULL ,
	 EndTime             datetime ,
	 Duration            TIME ,
	 IDUser              integer  NOT NULL ,
     CONSTRAINT  XPKSession  PRIMARY KEY  CLUSTERED ( IDSes  ASC) ,
     CONSTRAINT  R_6  FOREIGN KEY ( IDUser ) REFERENCES  User ( IDUser ) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE  Played 
( 
	 IDSes               integer  NOT NULL ,
	 IDGame              integer  NOT NULL ,
	 IDPlay              integer   AUTO_INCREMENT  NOT NULL ,
	 StartTime           datetime  NOT NULL ,
	 EndTime             datetime  NOT NULL ,
	 Duration            TIME  NOT NULL ,
	 TotalWager          decimal(13,5)  NOT NULL ,
	 TotalReceived       decimal(13,5)  NOT NULL ,
	 Profit              decimal(13,5)  NOT NULL ,
     CONSTRAINT  XPKPlayed  PRIMARY KEY  CLUSTERED ( IDPlay  ASC) ,
     CONSTRAINT  R_7  FOREIGN KEY ( IDSes ) REFERENCES  Session ( IDSes ) ON DELETE CASCADE ON UPDATE CASCADE ,
     CONSTRAINT  R_8  FOREIGN KEY ( IDGame ) REFERENCES  Game ( IDGame ) ON DELETE CASCADE ON UPDATE CASCADE
);


-- Inserts:
INSERT INTO privilege (Name) VALUES ('admin'), ('user');
INSERT INTO game (Name) VALUES ('BookOfGoldenSands'), ('SugarRush');
