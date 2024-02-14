go

create view PriceInfo
as
select A.IdArt as article, A.Price as price, S.IdSho as shop, S.Discount as discount
from Article A, Shop S
where A.IdSho = S.IdSho

go

----------------------------------------------------------

CREATE PROCEDURE SP_FINAL_PRICE 
	@idOrd int
AS
BEGIN
	
	declare @sum_full decimal(10,3) = 0
	declare @sum_discount decimal(10,3) = 0

	declare @idArt int, @amount int
	declare @kursor cursor

	set @kursor = cursor for
	select IdArt, Amount
	from Item
	where @idOrd = IdOrd

	open @kursor

	fetch from @kursor
	into @idArt, @amount

	while @@FETCH_STATUS = 0
	begin
		
		declare @price_full decimal(10,3), @price_discount decimal(10,3), @idSho int

		select @price_full = price * @amount, 
			@price_discount = price * @amount * (1 - discount * 0.01),
			@idSho = shop
		from PriceInfo
		where @idArt = article

		--------------

		if not exists(select * from Shop_Profit where IdOrd = @idOrd and IdSho = @idSho)
			insert into Shop_Profit(IdOrd, IdSho, Amount, SysPercentage) values(@idOrd, @idSho, @price_discount, 0)
		else
			update Shop_Profit set Amount = Amount + @price_discount where IdOrd = @idOrd and IdSho = @idSho

		--------------

		set @sum_full = @sum_full + @price_full
		set @sum_discount = @sum_discount + @price_discount
		
		fetch from @kursor
		into @idArt, @amount

	end

	close @kursor
	deallocate @kursor

	------------------ SYS PERCENTAGE

	declare @idBuy int
	select @idBuy = IdBuy from [Order] where IdOrd = @idOrd

	declare @spent_last_month decimal(10,3)

	select @spent_last_month = sum(FinalPrice)
	from [Order]
	where @idBuy = IdBuy and @idOrd <> IdOrd and [State] = 'arrived' and DATEDIFF(DAY, ReceivedTime, GETDATE()) < 30

	if @spent_last_month > 10000
	begin
		update [Order] set FinalPrice = @sum_discount * 0.98, DiscountSum = @sum_full - @sum_discount * 0.98 
		where IdOrd = @idOrd

		update Shop_Profit set Amount = Amount * 0.95, SysPercentage = 3
		where IdOrd = @idOrd
	end
	else
	begin
		update [Order] set FinalPrice = @sum_discount, DiscountSum = @sum_full - @sum_discount
		where IdOrd = @idOrd

		update Shop_Profit set Amount = Amount * 0.95, SysPercentage = 5
		where IdOrd = @idOrd
	end

	------------------ create transaction
	update Buyer set Credit = Credit - (select FinalPrice from [Order] where IdOrd = idOrd) where IdBuy = @idBuy
	insert into [Transaction](IdBuy, IdOrd, Amount) values(@idBuy, @idOrd, (select FinalPrice from [Order] where IdOrd = idOrd))
	------------------

END
GO

--------------------------------------------------------

CREATE TRIGGER TR_TRANSFER_MONEY_TO_SHOPS
ON  [Order]
   AFTER UPDATE
AS 
BEGIN

	declare @idOrd int, @idBuy int, @final_price decimal(10,3)

	declare @kursor1 cursor

	set @kursor1 = cursor for
	select IdOrd, IdBuy, FinalPrice
	from inserted
	where [State] = 'arrived'

	open @kursor1

	fetch from @kursor1
	into @idOrd, @idBuy, @final_price

	while @@FETCH_STATUS = 0
	begin

		declare @idSho int, @amount decimal(10,3), @sys_percentage int
		declare @idSys int

		declare @kursor2 cursor

		if NOT EXISTS (select * from [System] where Name = 'System')
			insert into [System](Name) values ('System')
		select @idSys = idSys from [System] where Name = 'System';

		set @kursor2 = cursor for
		select IdSho, Amount, SysPercentage
		from Shop_Profit
		where IdOrd = @idOrd

		open @kursor2

		fetch from @kursor2
		into @idSho, @amount, @sys_percentage

		while @@FETCH_STATUS = 0
		begin 

			if NOT EXISTS (select * from System_Profit where IdOrd = @idOrd and IdSys = @idSys)
				insert into System_Profit(IdOrd, IdSys, Amount) values (@idOrd, @idSys, @amount * @sys_percentage * 0.01 / 0.95)
			else
				update System_Profit set Amount = Amount + @amount * @sys_percentage * 0.01 / 0.95

			fetch from @kursor2
			into @idSho, @amount, @sys_percentage

		end

		close @kursor2
		deallocate @kursor2

		fetch from @kursor1
		into @idOrd, @idBuy, @final_price

	end

	close @kursor1
	deallocate @kursor1

END
GO

-------------------------------------------------------

CREATE PROCEDURE SP_RESEED_ID
AS
BEGIN
	DBCC CHECKIDENT (Article, RESEED, 0)
	
	DBCC CHECKIDENT (City, RESEED, -1)

	DBCC CHECKIDENT (Line, RESEED, 0)

	DBCC CHECKIDENT (Shop, RESEED, 0)

	DBCC CHECKIDENT (Buyer, RESEED, 0)

	DBCC CHECKIDENT ([Order], RESEED, 0)

	DBCC CHECKIDENT (Item, RESEED, 0)

	DBCC CHECKIDENT ([Transaction], RESEED, 0)

	DBCC CHECKIDENT ([System], RESEED, 0)

	DBCC CHECKIDENT (Shop_Profit, RESEED, 0)

	DBCC CHECKIDENT ([Path], RESEED, 0)

END
GO

