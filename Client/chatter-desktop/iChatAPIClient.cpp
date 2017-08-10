#include <QJsonDocument>
#include <QJsonObject>

#include "iChatAPIClient.h"

iChatAPIClient::iChatAPIClient()
{
  this->networkManager_ = new QNetworkAccessManager(this);
  baseUrl_ = "https://reqres.in/api/%1";
}

void iChatAPIClient::authenticateUser(const QString &username,
                                      const QString &password) {

  QUrl url(this->baseUrl_.arg("login/"));

  QNetworkRequest request(url);

  request.setHeader(QNetworkRequest::ContentTypeHeader, "application/json");

  connect(this->networkManager_, SIGNAL(finished(QNetworkReply*)),
          this, SLOT(authenticationCompleted(QNetworkReply*)));

  QJsonObject data;
  data["username"] = username;
  data["password"] = password;

  QJsonDocument dataDoc(data);

  this->networkManager_->post(request, dataDoc.toJson());
}

void iChatAPIClient::authenticationCompleted(QNetworkReply *reply) {
    QString data(reply->readAll());

    emit loginCompleted(true, data);
}
