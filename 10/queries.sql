USE homework;
-- 1. Найти номер модели, скорость и размер жесткого диска для всех ПК стоимостью менее 500 долларов.
SELECT model, speed, hd FROM pc WHERE price < 500;

-- 2. Найти производителей принтеров. Вывести поля: maker.
SELECT DISTINCT maker FROM product WHERE type = 'printer';

-- 3. Найти номер модели, объем памяти и размеры экранов ноутбуков, цена которых превышает 1000 долларов.
SELECT model, hd, screen FROM laptop WHERE price > 1000;

-- 4. Найти все записи таблицы Printer для цветных принтеров.
SELECT * FROM printer WHERE color = 'y';

-- 5. Найти номер модели, скорость и размер жесткого диска для ПК, имеющих скорость cd 12x или 24x и цену менее 600 долларов.
SELECT model, speed, hd FROM pc WHERE (cd = '12x' OR cd = '24x') AND price < 600;

-- 6. Указать производителя и скорость для тех ноутбуков, которые имеют жесткий диск объемом не менее 100 Гбайт.
SELECT product.maker, laptop.speed
FROM product JOIN laptop ON product.model = laptop.model
WHERE laptop.hd >= 100;

-- 7. Найти номера моделей и цены всех продуктов (любого типа), выпущенных производителем B (латинская буква).
SELECT product.model, laptop.price
FROM laptop JOIN product ON laptop.model = product.model
WHERE product.maker = 'B'
	UNION
SELECT product.model, pc.price
FROM pc JOIN product ON pc.model = product.model
WHERE product.maker = 'B'
	UNION
SELECT product.model, printer.price
FROM printer JOIN product ON product.model = printer.model
WHERE product.maker = 'B';

-- 8. Найти производителя, выпускающего ПК, но не ноутбуки.
SELECT DISTINCT maker 
FROM product 
WHERE type = 'pc' 
	AND maker NOT IN (SELECT maker FROM product WHERE type = 'laptop');
    
-- 9. Найти производителей ПК с процессором не менее 450 Мгц. Вывести поля: maker.
SELECT DISTINCT product.maker 
FROM product JOIN pc ON pc.model = product.model 
WHERE pc.speed >= 450;

-- 10. Найти принтеры, имеющие самую высокую цену. Вывести поля: model, price.
SELECT model, price 
FROM printer 
WHERE price = (SELECT MAX(price) FROM printer);

-- 11. Найти среднюю скорость ПК.
SELECT AVG(speed) FROM pc;

-- 12. Найти среднюю скорость ноутбуков, цена которых превышает 1000 долларов.
SELECT AVG(speed) 
FROM laptop
WHERE price > 1000;

-- 13. Найти среднюю скорость ПК, выпущенных производителем A
SELECT AVG(speed) 
FROM pc JOIN product ON pc.model = product.model
WHERE product.maker = 'A';

-- 14. Для каждого значения скорости процессора найти среднюю стоимость ПК с такой же скоростью. 
-- Вывести поля: скорость, средняя цена.
SELECT DISTINCT speed, AVG(price) AS average_price
FROM pc
GROUP BY speed;

-- 15. Найти размеры жестких дисков, совпадающих у двух и более PC. Вывести поля: hd.
SELECT hd 
FROM pc 
GROUP BY hd
HAVING COUNT(hd) >= 2;

-- 16. Найти пары моделей PC, имеющих одинаковые скорость процессора и RAM. 
-- В результате каждая пара указывается только один раз, т.е. (i,j), но не (j,i), 
-- Порядок вывода полей: модель с большим номером, модель с меньшим номером, скорость, RAM.
SELECT DISTINCT LEAST(pc1.model, pc2.model), 
	GREATEST(pc1.model, pc2.model),
    pc1.speed,
	pc1.ram
FROM pc pc1 JOIN pc pc2
WHERE pc1.speed = pc2.speed 
AND pc1.model != pc2.model 
AND pc1.ram = pc2.ram;

-- 17. Найти модели ноутбуков, скорость которых меньше скорости любого из ПК. Вывести поля: type, model, speed.
SELECT product.type, product.model, laptop.speed 
FROM laptop JOIN product ON laptop.model = product.model
WHERE laptop.speed < (SELECT MIN(speed) FROM pc);

-- 18. Найти производителей самых дешевых цветных принтеров. Вывести поля: maker, price.
SELECT product.maker, printer.price 
FROM product JOIN printer ON product.model = printer.model
WHERE printer.price = (SELECT MIN(price) FROM printer)
	AND printer.color = 'y';

-- 19. Для каждого производителя найти средний размер экрана выпускаемых им ноутбуков. 
-- Вывести поля: maker, средний размер экрана.
SELECT product.maker, AVG(laptop.screen)
FROM product JOIN laptop ON laptop.model = product.model
GROUP BY product.maker;

-- 20. Найти производителей, выпускающих по меньшей мере три различных модели ПК. Вывести поля: maker, число моделей.
SELECT product.maker as maker, COUNT(product.model) AS pc_number FROM product 
JOIN pc ON pc.model = product.model
GROUP BY product.maker
HAVING pc_number >= 3;

-- 21. Найти максимальную цену ПК, выпускаемых каждым производителем. Вывести поля: maker, максимальная цена.
SELECT product.maker, MAX(pc.price) 
FROM product JOIN pc ON pc.model = product.model
GROUP BY product.maker;

-- 22. Для каждого значения скорости процессора ПК, превышающего 600 МГц, найти среднюю цену ПК с такой же скоростью. 
-- Вывести поля: speed, средняя цена.
SELECT speed, AVG(price)
FROM pc
WHERE speed > 600
GROUP BY speed;

-- 23. Найти производителей, которые производили бы как ПК, так и ноутбуки со скоростью не менее 750 МГц. 
-- Вывести поля: maker
WITH maker_pc AS (SELECT DISTINCT product.maker
FROM product JOIN pc ON product.model = pc.model
WHERE pc.speed >= 750),

maker_laptop AS (SELECT DISTINCT product.maker
FROM product JOIN laptop ON product.model = laptop.model
WHERE laptop.speed >= 750)

SELECT DISTINCT maker FROM product 
WHERE maker IN (SELECT * FROM maker_pc) 
	AND maker IN (SELECT * FROM maker_laptop)

-- 24. Перечислить номера моделей любых типов, имеющих самую высокую цену по всей имеющейся в базе данных продукции.
SELECT model, price
FROM (SELECT model, price FROM laptop
    UNION ALL
    SELECT model, price FROM pc
    UNION ALL
    SELECT model, price FROM printer) as all_models
WHERE price = (SELECT MAX(price) FROM (
        SELECT price FROM laptop
        UNION ALL
        SELECT price FROM pc
        UNION ALL
        SELECT price FROM printer) as max_price
);

-- 25. Найти производителей принтеров, которые производят ПК с наименьшим объемом RAM и с 
-- самым быстрым процессором среди всех ПК, имеющих наименьший объем RAM. Вывести поля: maker
WITH min_ram as (SELECT MIN(ram) FROM pc),
makers_printer as (SELECT DISTINCT product.maker
FROM product 
WHERE type = 'printer')

SELECT product.maker
FROM product JOIN pc ON pc.model = product.model
WHERE pc.ram = (SELECT * FROM min_ram)
	AND pc.speed = (SELECT MAX(speed) FROM pc WHERE ram = (SELECT * FROM min_ram))
	AND product.maker IN (SELECT * FROM makers_printer)





