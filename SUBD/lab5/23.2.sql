Drop table if exists #CostRep
Create table #CostRep (DishName NVarchar(255), ProductionCost Float)
Insert into #CostRep 
select название, Цена from VPrice

Select * from #CostRep order by ProductionCost
Select top 3 * from #CostRep order by ProductionCost desc