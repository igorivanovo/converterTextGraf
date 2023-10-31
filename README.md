title: Курсовой проект «Конвертер в текстовую графику»
description: Наша цель - разработать класс поискового движка, который способен быстро находить указанное слово среди pdf-файлов, причём ранжировать результаты по количеству вхождений. Также у нас будет сервер, который обслуживает входящие запросы с помощью этого движка.
category: Учебный проект
icon: bx bx-message-detail
info: Репозиаторий на GitHab
link:https://github.com/igorivanovo/converterTextGraf.git



# «Конвертер в текстовую графику»

Нас пригласили поучаствовать в разработке приложения, умеющего скачивать картинки по URL и конвертировать изображения в
текстовую графику (т. е. в текст из разных символов, которые в совокупности выглядят как изображение). Вот пример его
работы. Картинка на нём — это текст из мелких символов:

<img src="/static/img/preview.png"  alt="" width="600">


## Структура проекта

<br>
<table>
<thead>
<tr>
      <th> Класс / Интерфейс </th>
      <th>Для чего</th>
</tr> 
</thead> 
<tbody>   
<tr>
<td><span style="color:red;">BadImageSizeException </span></td>
<td>Класс исключения, которое мы выбрасываем</td>
</tr>
<tr>
<td><span style="color:red;">TextColorSchema</span></td>
<td>Интерфейс цветовой схемы, который    мы реализуем</td>
</tr>
<tr>
<td><span style="color:red;">TextGraphicsConverter </span></td>
<td>Интерфейс конвертера картинок, который мы реализуем</td>
</tr>
<tr>
<td><span style="color:red;">GServer </span></td>
<td>Класс сервера, который будет использует наш конвертер</td>
</tr>
<tr>
<td><span style="color:red;">Main </span></td>
<td>Запуск приложения. В нём запускается сервер, также в нём можно будет конвертировать картинки в текстовые файлы без сервера</td>
</tr>
<tr>
<td></td>
<td></td>
</tr>
</tbody>
</table>

## Требования к конвертеру

Самый главный метод — это метод `convert`, который принимает параметром URL в виде текста, например, `«https://raw.githubusercontent.com/netology-code/java-diplom/main/pics/simple-test.png»`, внутри метода качает и анализирует картинку, после чего отдаёт значение типа `String`, в котором содержится это изображение в виде текстовой графики.

В Java `String` — это текст и не обязательно всего одна строчка текста. Т. е. в один объект типа `String` можно занести многострочный текст,
а разделителем строк (по сути Enter) будет специальный символ, который пишется в коде как `\n`. В итоге у вас в конце каждой строчки текстового изображения будет символ переноса строки (запись в коде — `\n`).

Пример работы конвертера, результат которого выводим в консоль:

`String url = "https://raw.githubusercontent.com/netology-code/java-diplom/main/pics/simple-test.png";`<br>
`String imgTxt = converter.convert(url);`<br>
`System.out.println(imgTxt);`<br>


В итоге мы видим такой результат, где более тёмные участки заменяются на более жирные символы, а светлые на более незаметные символы:
<img src="/static/img/simple-test-demo.jpeg"  alt="" width="600">


Также интерфейс конвертера требует от него возможность выставлять ему настройки перед конвертацией:

- максимально допустимое соотношение сторон (ширины и высоты). Если метод не вызывали, то любое соотношение допустимо;
- максимально допустимую высоту итогового изображения. Если метод не вызывали, то любая высота допустима;
- максимально допустимую ширину итогового изображения. Если метод не вызывали, то любая ширина допустима;
- текстовую цветовую схему — объект специального интерфейса, который будет отвечать за превращение степени белого (числа от 0 до 255) в символ. Если метод не вызывали, должен использоваться объект написанного вами класса как значение по умолчанию.

Например, следующий конвертер не должен конвертировать, если ширина больше длины в три раза, т. к. максимальное соотношение сторон ему выставлено в 2:


`TextGraphicsConverter converter = ...;`// Создайте тут объект нашего класса конвертера<br>
`converter.setMaxRatio(2);` // выставляет максимально допустимое соотрношение сторон картинки<br>
`String imgTxt = converter.convert(...);`// для слишком широкой картинки должно выброситься исключение BadImageSizeException.<br>


Когда вы передадите конвертер серверу, он выставит ему свои желаемые параметры, которые будут влиять на конвертацию:

<img src="/static\img\convert.png"  alt="">



При этом при реализации конвертера информацию об этих конкретных числах мы не используем. Наш конвертер может уметь работать с любыми настройками, описанными выше. Т. е. если в сервере поменяют настройки конвертеру, наш конвертер без изменений кода должен работать с новыми значениями.

Общая схема работы метода `convert` будет соответствовать последовательности действий (подробнее описаны ниже):

1. Скачиваем картинку по URL.
2. Менеджеру могли выставить максимально допустимое соотношение сторон (ширины и высоты); если оно слишком большое, то конвертация не делается и выбрасывается исключение.
3. При конвертации мы будем менять каждый пиксель на символ: чем пиксель темнее, тем жирнее символ, который мы подставим. Менеджеру могли выставить максимальные ширину и высоту итоговой картинки, при этом если исходная картинка больше, то нам надо уменьшить её размер, соблюдая пропорции.
4. Превращаем цветное изображение в чёрно-белое, чтобы мы смотрели только на интенсивность цвета, а не подбирали для красного одни символы, для зелёного другие и т. п.
1. Перебираем все пиксели изображения, спрашивая у них степень белого (число от 0 до 255, где 0 — это чёрный, а 255 — это светлый). В зависимости от этого числа выбираем символ из заранее подготовленного набора.
1. Собираем все полученные символы в единую строку, отдаём как результат конвертации.

Костяк метода `convert`:

`@Override`<br>
`public String convert(String url) throws IOException,BadImageSizeException {`<br>
// Вот так просто мы скачаем картинку из интернета :<br>
&ensp;`BufferedImage img = ImageIO.read(new URL(url));`<br>
// Если конвертер попросили проверять на максимально допустимое<br>
// соотношение сторон изображения, то делаем эту проверку,<br>
// и, если картинка не подходит, выбросить исключение BadImageSizeException.<br>
// Чтобы получить ширину картинки, вызываем img.getWidth(), высоту - img.getHeight()<br>

// Если конвертеру выставили максимально допустимые ширину и/или высоту,<br>
//  по текущим высоте и ширине вычисляем новые высоту и ширину.<br>
// Соблюдение пропорций означает, что мы должны уменьшать ширину и высоту<br>
// в одинаковое количество раз.<br>
// Пример 1: макс. допустимые 100x100, а картинка 500x200. Новый размер<br>
// будет 100x40 (в 5 раз меньше).<br>
// Пример 2: макс. допустимые 100x30, а картинка 150x15. Новый размер<br>
// будет 100x10 (в 1.5 раза меньше)<br>
// Теперь нам нужно попросить картинку изменить свои размеры на новые.<br>
// Последний параметр означает, что мы просим картинку плавно сузиться<br>
// на новые размеры. В результате мы получаем ссылку на новую картинку, которая<br>
// представляет собой суженную старую.<br>
&ensp;`Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOT)`<br>
// Теперь сделаем её чёрно-белой. Для этого :<br>
// Создадим новую пустую картинку нужных размеров, заранее указав последним<br>
// параметром чёрно-белую цветовую палитру:<br>
&ensp;`BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);`<br>
// Попросим у этой картинки инструмент для рисования на ней:<br>
&ensp;`Graphics2D graphics = bwImg.createGraphics();`<br>
// А этому инструменту скажем, чтобы он скопировал содержимое из нашей суженной картинки:<br>
&ensp;`graphics.drawImage(scaledImage, 0, 0, null);`<br>

// Теперь в bwImg у нас лежит чёрно-белая картинка нужных нам размеров.<br>
// Мы можете отслеживать каждый из этапов, в любом удобном для<br>
// вас моменте сохранив промежуточную картинку в файл через:<br>
// ImageIO.write(imageObject, "png", new File("out.png"));<br>
// После вызова этой инструкции у нас в проекте появится файл картинки out.<br>
// Теперь  пройдёмся по пикселям нашего изображения.<br>
// Если для рисования мы просили у картинки .createGraphics(),<br>
// то для прохода по пикселям нам нужен будет этот инструмент:<br>
&ensp;`WritableRaster bwRaster = bwImg.getRaster;`<br>
// Он хорош тем, что у него мы можем спросить пиксель на нужных<br>
// нам координатах, указав номер столбца (w) и строки (h)<br>
// int color = bwRaster.getPixel(w, h, new int[3])[0];<br>
// Выглядит странно? Согласен. Сам возвращаемый методом пиксель — это <br>
// массив из трёх интов, обычно это интенсивность красного, зелёного и синего.<br>
// Но у нашей чёрно-белой картинки цветов нет, и нас интересует<br>
// только первое значение в массиве. Ещё мы параметром передаём интовый массив на три ячейки.<br>
// Дело в том, что этот метод не хочет создавать его сам и просит<br>
// нас сделать это, а сам метод лишь заполнит его и вернёт.<br>
// Потому что создавать массивы каждый раз слишком медленно. Мы можем создать<br>
// массив один раз, сохранить в переменную и передавать один<br>
// и тот же массив в метод, ускорив тем самым програм<br>
//  Осталось пробежаться двойным циклом по всем столбцам (ширина)<br>
// и строкам (высота) изображения, на каждой внутренней итерации<br>
// получить степень белого пикселя (int color выше) и по ней<br>
// получить соответствующий символ c. Логикой превращения цвета<br>
// в символ будет заниматься другой объект<br>
`for ??? {`<br>
&ensp;`for ??? {`<br>
&ensp;`int color = bwRaster.getPixel(w, h, new int[3])[0]; `<br>
&ensp;`char c = schema.convert(color);` <br>
&ensp;`???` //запоминаем символ c<br>
&ensp;`}`<br>
`}`<br>

// Осталось собрать все символы в один большой текст.<br>
// Для того, чтобы изображение не было слишком узким, <br>
// каждый пиксель превращаем в два повторяющихся символа, полученных<br>
// от схемы<br>
// Возвращаем собранный текст.<br>
`}`<br>


## Требования к цветовой схеме
Мы написали интерфейс конвертера так, чтобы сам он не подбирал каждому цвету определённый <br>символ, но чтобы им занимался другой объект следующего интерфейса:<br>


`public interface TextColorSchema {`<br>
&ensp; `char convert(int color);`<br>
`}`<br>




