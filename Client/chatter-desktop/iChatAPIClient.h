#ifndef ICHATAPICLIENT_H
#define ICHATAPICLIENT_H

#include <QtNetwork/QNetworkAccessManager>
#include <QtNetwork/QNetworkReply>

class iChatAPIClient : public QObject
{
  Q_OBJECT
public:
    iChatAPIClient();

    void authenticateUser(const QString& username, const QString& password);

private slots:
    void authenticationCompleted(QNetworkReply* reply);


signals:
    void loginCompleted(bool status, const QString& token);

private:
    QNetworkAccessManager* networkManager_;
    QString baseUrl_;

};

#endif // ICHATAPICLIENT_H
