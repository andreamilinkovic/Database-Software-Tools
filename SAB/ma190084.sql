
CREATE TABLE [Article]
( 
	[IdArt]              integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NULL ,
	[IdSho]              integer  NOT NULL ,
	[Price]              integer  NULL 
	CONSTRAINT [Nula_1706928512]
		 DEFAULT  0
	CONSTRAINT [VeceJednakoNula_1099885269]
		CHECK  ( Price >= 0 ),
	[Amount]             integer  NULL 
	CONSTRAINT [Nula_1664842265]
		 DEFAULT  0
	CONSTRAINT [VeceJednakoNula_1463422543]
		CHECK  ( Amount >= 0 )
)
go

CREATE TABLE [Buyer]
( 
	[IdBuy]              integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NOT NULL ,
	[IdCit]              integer  NOT NULL ,
	[Credit]             decimal(10,3)  NULL 
)
go

CREATE TABLE [City]
( 
	[IdCit]              integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NULL 
)
go

CREATE TABLE [Item]
( 
	[IdOrd]              integer  NOT NULL ,
	[IdIte]              integer  IDENTITY  NOT NULL ,
	[Amount]             integer  NULL 
	CONSTRAINT [Nula_592995767]
		 DEFAULT  0
	CONSTRAINT [VeceJednakoNula_2018280849]
		CHECK  ( Amount >= 0 ),
	[IdArt]              integer  NOT NULL 
)
go

CREATE TABLE [Line]
( 
	[IdLin]              integer  IDENTITY  NOT NULL ,
	[IdCit1]             integer  NOT NULL ,
	[IdCit2]             integer  NOT NULL ,
	[Distance]           integer  NULL 
	CONSTRAINT [Nula_1085714848]
		 DEFAULT  0
	CONSTRAINT [VeceJednakoNula_1634329565]
		CHECK  ( Distance >= 0 )
)
go

CREATE TABLE [Order]
( 
	[IdOrd]              integer  IDENTITY  NOT NULL ,
	[State]              varchar(100)  NULL 
	CONSTRAINT [OrderStatus_233897343]
		CHECK  ( [State]='created' OR [State]='sent' OR [State]='arrived' ),
	[IdBuy]              integer  NOT NULL ,
	[FinalPrice]         decimal(10,3)  NULL 
	CONSTRAINT [Nula_1795033884]
		 DEFAULT  0
	CONSTRAINT [VeceJednakoNula_1514262752]
		CHECK  ( FinalPrice >= 0 ),
	[DiscountSum]        decimal(10,3)  NULL 
	CONSTRAINT [Nula_403757803]
		 DEFAULT  0
	CONSTRAINT [VeceJednakoNula_686744496]
		CHECK  ( DiscountSum >= 0 ),
	[SentTime]           datetime  NULL ,
	[ReceivedTime]       datetime  NULL ,
	[Location]           integer  NULL 
)
go

CREATE TABLE [Path]
( 
	[IdCit]              integer  NOT NULL ,
	[NumDays]            integer  NULL ,
	[IdOrd]              integer  NOT NULL ,
	[IdPat]              integer  IDENTITY  NOT NULL 
)
go

CREATE TABLE [Shop]
( 
	[IdSho]              integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NULL ,
	[Discount]           integer  NULL ,
	[IdCit]              integer  NOT NULL 
)
go

CREATE TABLE [Shop_Profit]
( 
	[Amount]             decimal(10,3)  NULL ,
	[SysPercentage]      integer  NULL ,
	[IdSho]              integer  NOT NULL ,
	[IdOrd]              integer  NOT NULL ,
	[IdTra]              integer  IDENTITY  NOT NULL 
)
go

CREATE TABLE [System]
( 
	[IdSys]              integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NULL 
)
go

CREATE TABLE [System_Profit]
( 
	[Amount]             decimal(10,3)  NULL ,
	[IdOrd]              integer  NOT NULL ,
	[IdSys]              integer  NOT NULL 
)
go

CREATE TABLE [Transaction]
( 
	[IdTra]              integer  IDENTITY  NOT NULL ,
	[IdOrd]              integer  NOT NULL ,
	[IdBuy]              integer  NOT NULL ,
	[Amount]             decimal(10,3)  NULL 
	CONSTRAINT [Nula_18743156]
		 DEFAULT  0
	CONSTRAINT [VeceJednakoNula_891964203]
		CHECK  ( Amount >= 0 ),
	[ExecutionTime]      datetime  NULL 
)
go

ALTER TABLE [Article]
	ADD CONSTRAINT [XPKArticle] PRIMARY KEY  CLUSTERED ([IdArt] ASC)
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [XPKBuyer] PRIMARY KEY  CLUSTERED ([IdBuy] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdCit] ASC)
go

ALTER TABLE [Item]
	ADD CONSTRAINT [XPKItem] PRIMARY KEY  CLUSTERED ([IdIte] ASC)
go

ALTER TABLE [Line]
	ADD CONSTRAINT [XPKLine] PRIMARY KEY  CLUSTERED ([IdLin] ASC)
go

ALTER TABLE [Order]
	ADD CONSTRAINT [XPKOrder] PRIMARY KEY  CLUSTERED ([IdOrd] ASC)
go

ALTER TABLE [Path]
	ADD CONSTRAINT [XPKPath] PRIMARY KEY  CLUSTERED ([IdPat] ASC)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XPKShop] PRIMARY KEY  CLUSTERED ([IdSho] ASC)
go

ALTER TABLE [Shop_Profit]
	ADD CONSTRAINT [XPKShop_Profit] PRIMARY KEY  CLUSTERED ([IdTra] ASC)
go

ALTER TABLE [System]
	ADD CONSTRAINT [XPKSystem] PRIMARY KEY  CLUSTERED ([IdSys] ASC)
go

ALTER TABLE [System_Profit]
	ADD CONSTRAINT [XPKSystem_Profit] PRIMARY KEY  CLUSTERED ([IdOrd] ASC,[IdSys] ASC)
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [XPKTransaction] PRIMARY KEY  CLUSTERED ([IdTra] ASC)
go


ALTER TABLE [Article]
	ADD CONSTRAINT [R_23] FOREIGN KEY ([IdSho]) REFERENCES [Shop]([IdSho])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdCit]) REFERENCES [City]([IdCit])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Item]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([IdOrd]) REFERENCES [Order]([IdOrd])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Item]
	ADD CONSTRAINT [R_24] FOREIGN KEY ([IdArt]) REFERENCES [Article]([IdArt])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Line]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([IdCit1]) REFERENCES [City]([IdCit])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Line]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IdCit2]) REFERENCES [City]([IdCit])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Order]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([IdBuy]) REFERENCES [Buyer]([IdBuy])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Path]
	ADD CONSTRAINT [R_34] FOREIGN KEY ([IdCit]) REFERENCES [City]([IdCit])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Path]
	ADD CONSTRAINT [R_35] FOREIGN KEY ([IdOrd]) REFERENCES [Order]([IdOrd])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Shop]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([IdCit]) REFERENCES [City]([IdCit])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Shop_Profit]
	ADD CONSTRAINT [R_36] FOREIGN KEY ([IdSho]) REFERENCES [Shop]([IdSho])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Shop_Profit]
	ADD CONSTRAINT [R_37] FOREIGN KEY ([IdOrd]) REFERENCES [Order]([IdOrd])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [System_Profit]
	ADD CONSTRAINT [R_18] FOREIGN KEY ([IdOrd]) REFERENCES [Order]([IdOrd])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [System_Profit]
	ADD CONSTRAINT [R_19] FOREIGN KEY ([IdSys]) REFERENCES [System]([IdSys])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([IdOrd]) REFERENCES [Order]([IdOrd])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([IdBuy]) REFERENCES [Buyer]([IdBuy])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go
