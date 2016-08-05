# PassManager

PassManager intends to be a simple  application for storing passwords associated with a domain and a user-account securely (password-vault). This application is written in JavaFX.

**Since we want to achieve a maximum in security  this app needs the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 8 to be installed manually to unlock strong crypto-keylength**
************
**Um ein Maximum an Sicherheit zu erreichen benötigt diese Anwendung die manuelle Installation der Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 8, um ausreiched breite Schlüssellängen zu ermöglichen**


### Funktionsweise
Die Anwendung soll auf zweierlei Arten einsetzbar sein:
  - Als klassicher Password-Tresor (speicherung von Daten innerhalb der Anwendung)
  - "portable"- Pass-Generator-Modus (keinerlei Datenspeicherung die Anwendung generiert ein eindeutiges Password für die Kombination aus Masterpassword + domain + username)

 Als "Tresor" legt der Benutzer ein password für eine domain und einen dazugehörigen user-account in einer Liste an. Diese Liste lässt sich anschließend ex- und importieren. Diese Liste wird durch das Masterpassword geschützt, nach entschlüsselung stehen alle Einträge in der Übersicht bereit.

 Als "Generator" wird aus der der kombination aus domain, dem user-account und dem masterpassword ein eindeutiges password abgeleitet. Dies hat den vorteil, dass keinerlei benutzerdaten gespeichert werden müssen - die Anwedung dient dabei lediglich als Generator für ein möglichst sicheres (= schwer zu merkendes) password. Als benutzer muss man sich somit nur ein sehr starkes masterpassword merken und kann dann für jede domain und einen dazugehörigen username seine passörter generieren und jederzeit auch wieder abfragen. Allerdings ist dabei die exakte schreibweise der domain und username von höhster wichtigkeit, ändert man nur ein zeichen so generiert dies ein völlig anderes password.  Beispiel: generiert man sich  ein pass für domain "google" und den dortigen useraccount "müller" und schreibt bei nächsten mal bei der passwordgenerierung bspw. "google.de" und "Müller" generiert dies ein völlig anderes password.

#### Funktionsweise
You can also:
  - Import and save files from GitHub, Dropbox, Google Drive and One Drive
  - Drag and drop files into Dillinger
  - Export documents as Markdown, HTML and PDF

> The overriding design goal for Markdown's
> formatting syntax is to make it as readable
> as possible. The idea is that a
> Markdown-formatted document should be
> publishable as-is, as plain text, without
> looking like it's been marked up with tags
> or formatting instructions.

This text you see here is *actually* written in Markdown! To get a feel for Markdown's syntax, type some text into the left window and watch the results in the right.

### Version
3.5.1

### Tech

Dillinger uses a number of open source projects to work properly:

* [AngularJS] - HTML enhanced for web apps!
* [Ace Editor] - awesome web-based text editor
* [markdown-it] - Markdown parser done right. Fast and easy to extend.
* [Twitter Bootstrap] - great UI boilerplate for modern web apps
* [node.js] - evented I/O for the backend
* [Express] - fast node.js network app framework [@tjholowaychuk]
* [Gulp] - the streaming build system
* [keymaster.js] - awesome keyboard handler lib by [@thomasfuchs]
* [jQuery] - duh

And of course Dillinger itself is open source with a [public repository][dill]
 on GitHub.

### Installation

Dillinger requires [Node.js](https://nodejs.org/) v4+ to run.

Download and extract the [latest pre-built release](https://github.com/joemccann/dillinger/releases).

Install the dependencies and devDependencies and start the server.

```sh
$ cd dillinger
$ npm install -d
$ node app
```

For production environments...

```sh
$ npm install --production
$ npm run predeploy
$ NODE_ENV=production node app
```

### Plugins

Dillinger is currently extended with the following plugins

* Dropbox
* Github
* Google Drive
* OneDrive

Readmes, how to use them in your own application can be found here:

* [plugins/dropbox/README.md] [PlDb]
* [plugins/github/README.md] [PlGh]
* [plugins/googledrive/README.md] [PlGd]
* [plugins/onedrive/README.md] [PlOd]

### Development

Want to contribute? Great!

Dillinger uses Gulp + Webpack for fast developing.
Make a change in your file and instantanously see your updates!

Open your favorite Terminal and run these commands.

First Tab:
```sh
$ node app
```

Second Tab:
```sh
$ gulp watch
```

(optional) Third:
```sh
$ karma start
```
#### Building for source
For production release:
```sh
$ gulp build --prod
```
Generating pre-built zip archives for distribution:
```sh
$ gulp build dist --prod
```
### Docker