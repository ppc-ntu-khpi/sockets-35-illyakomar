# Створення сокет-клієнта
![](socket.png)

Ця робота доповнює основний цикл лабораторних робіт #1-8 (проект **Banking**, [Netbeans](https://netbeans.org/)) з ООП на третьому курсі [ППК НТУ "ХПІ](http://polytechnic.poltava.ua)". Основна мета роботи - познайомитись з мережевими можливостями Java на прикладі сокетних комунікацій. Згадувані 'базові' роботи розміщено в [окремому репозиторії](https://github.com/liketaurus/OOP-JAVA).

##  Архітектура системи
В цій роботі ви напишете код для під'єднання чат-клієнта до сервера (код сервера надається). Чат-сервер відповідає за надсилання повідомлень, отриманих від одного клієнта, усім підключеним клієнтам (включаючи оригінального відправника). На рисунку показано діаграму архітектури системи, в якій кілька клієнтів приєднані до одного чат-сервера. У цьому сценарії Саймона пише This is cool! у текстовому полі, призначеному для тексту повідомлення, та вказує своє ім'я (Саймон) у відповідному текстовому полі клієнта і надсилає  його на сервер через вихідний потік (крок 1). Сервер отримує повідомлення, а потім пересилає повідомлення кожному приєднаному клієнту (кроки 2–4). При цьому порядок переданих повідомлень не є важливим.

![](https://github.com/ppc-ntu-khpi/Sockets-Starter/blob/master/Client-Server.png)

Клієнт чату (наданий вам [код](https://github.com/ppc-ntu-khpi/Sockets-Starter/blob/master/classes/ChatClient.java)) має бути змінений, аби додати дві важливі функції: 
* надсилання повідомлення користувача до сервера
* відображення всіх отриманих від сервера повідомлень

На рисунку показано детальну будову програми ChatClient. Вам потрібно додати метод **doConnect** до класу **ChatClient** для ініціації сокетного під'єднання до чат-сервера.

![](https://github.com/ppc-ntu-khpi/Sockets-Starter/blob/master/ChatClient.png)

**УВАГА!** *Основне завдання дозволить вам отримати "трійку". Завдання на "чотири" та "п'ять" описані нижче та включають в себе основне завдання!*

## Хід роботи
### Допрацювання чат-клієнту
1. створіть новий проект у середовищі розробки за вашим вибором
2. додайте в проект файл [ChatClient](https://github.com/ppc-ntu-khpi/Sockets-Starter/blob/master/classes/ChatClient.java) і відкрийте його
3. імпортуйте потрібні пакети:
````java
import java.net.*;
import java.io.*;
````
4. додайте до класу змінні для вхідного та вихідного потоків:
````java 
public class ChatClient {
  // решта коду
  private Socket connection = null;
  private BufferedReader serverIn = null;
  private PrintStream serverOut = null;
  // решта коду
}
````
5. додайте метод *doConnect* для ініціалізації сокетного з'єднання з сервером:
````java
private void doConnect() {
````
6. отримайте значення ІР-адреси сервера та порту:
````java
  String serverIP = System.getProperty("serverIP", "127.0.0.1");
  String serverPort = System.getProperty("serverPort", "2000");
````
Зверніть увагу, що адреса та порт сервера тут читаються з системних властивостей, а надані у лапках значення - це значення за замовчуванням, які слід застосувати якщо відповідних змінних не знайдено. 
7. створіть з'єднання з чат-сервером:
````java
  try {
    connection = new Socket(serverIP, Integer.parseInt(serverPort));
````
8. отримайте вхідний поток та збережіть посилання на нього у змінній, яку ви створили на кроці 4:
````java
    InputStream is = connection.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    serverIn = new BufferedReader(isr);
````
9. отримайте вихідний потік та збережіть посилання на нього у змінній, яку ви створили на кроці 4:
````java
    serverOut = new PrintStream(connection.getOutputStream());    
````
10. створіть потік для читання повідомлень:
````java
     Thread t = new Thread(new RemoteReader());
     t.start();
````
УВАГА! На цьому етапі ви отримаєте помилку, оскільки клас *RemoteReader* ще не описано - ми створимо його на одному з наступних кроків!
11. перехопіть виключення:
````java
    } catch (Exception e) {
      System.err.println("Unable to connect to server!");
      e.printStackTrace();
    }
} // закінчення методу doConnect
````
12. змініть метод *launchFrame* - додайте виклик щойно створеного методу *doConnect*:
````java
private void launchFrame() {
  // решта коду
  doConnect();
}
````
13. змініть внутрішній клас *SendHandler* - додайте логіку надсилання повідомлень (з ім'ям користувача) до вихідного потоку. Не забудьте видалити код, який відображає повідомлення у області повідомлень!
````java
private class SendHandler implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    String text = input.getText();
    text = usernames.getSelectedItem() + ": " + text + "\n";
    serverOut.print(text);
    input.setText("");
  } // закінчення методу actionPerformed 
} // закінчення опису внутрішнього класу SendHandler
````
14. створіть внутрішній клас *RemoteReader*, який реалізує інтерфейс *Runnable*. Метод *run* має читати рядок з вхідного потоку у нескінченному циклі:
````java
private class RemoteReader implements Runnable {
  public void run() {
    try {
      while ( true ) {
        String nextLine = serverIn.readLine();
        output.append(nextLine + "\n");
      }
    } catch (Exception e) {
        System.err.println("Error while reading from server.");
        e.printStackTrace()
      }
  } // закінчення методу run 
} // закінчення опису внутрішнього класу RemoteReader 
````
15. впевніться, що програма успішно компілюється

### Перевірка працездатності сервера та клієнта
1. додайте в проект решту файлів - вміст теки [Server](https://github.com/ppc-ntu-khpi/Sockets-Starter/tree/master/classes/Server)
2. запустіть сервер - клас *ChatServer*. Якщо ви використовуєте Netbeans - правою кнопкою на файлі та обрати пункт *Run File* або натиснути <kbd>Shift</kbd>+<kbd>F6</kbd>. УВАГА! Якщо в консолі ви отримаєте повідомлення про помилки читання чи створення файлу хостів - не звертайте уваги - головне аби серед інших повідомлень ви побачили повідомлення ````Started server on port 2000```` і процес сервера продовжив працювати!
3. запустіть клієнт - клас *ChatClient* тим же способом (<kbd>Shift</kbd>+<kbd>F6</kbd>). В області повідомлень ви маєте побачити магічну фразу😉 від сервера (Yippy Skippy) та повідомлення про з'єднання. УВАГА! Якщо ви отримаєте повідомлення від файрвола Windows - дозвольте вашим програмам доступ до мережі! А краще - взагалі тимчасово вимкнути файрвол!
4. все! можна спілкуватись! Запустіть ще один клієнт (тим же способом) і спробуйте в одному з них відправити якесь повідомлення - ви маєте побачити його в іншому клієнті. УВАГА! Якщо хочете початитись з другом, у пункті 6 вкажіть IP-адресу його машини!


### Завдання на "п'ять"
1. позбавтесь від використання **файлу хостів** в коді сервера (розберіться як він працює та змініть так аби позбавитись помилок при його запуску)
2. реалізуйте підтримку кирилиці - кодування **UTF-8** (потрібну для цього інформацію ви знайдете [тут](http://tutorials.jenkov.com/java-io/inputstreamreader.html)) 

## Результат

![Result1](/img/result.PNG)

![Result2](/img/result1.PNG)

![Result3](/img/result2.PNG)

---
**УВАГА! Не забувайте завантажувати результати виконання робіт до своїх репозиторіїв!**

![](https://img.shields.io/badge/Made%20with-JAVA-red.svg)
![](https://img.shields.io/badge/Made%20with-%20Netbeans-brightgreen.svg)
![](https://img.shields.io/badge/Made%20at-PPC%20NTU%20%22KhPI%22-blue.svg) 
