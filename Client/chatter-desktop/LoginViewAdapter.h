#ifndef LOGINVIEWADAPTER_H
#define LOGINVIEWADAPTER_H

#include <QWidget>
#include <QLineEdit>
#include <QPushButton>
#include <QtNetwork/QNetworkAccessManager>
#include <QtNetwork/QNetworkReply>

#include "iChatAPIClient.h"

namespace Ui {
class LoginViewAdapter;
}

class LoginViewAdapter : public QWidget
{
    Q_OBJECT

public:
    explicit LoginViewAdapter(QWidget *parent = 0);
    ~LoginViewAdapter();

private slots:
    void handleLoginButtonPressed();
    void handleLoginCompleted(bool status, const QString& data);

private:
    QLineEdit* usernameLineEdit;
    QLineEdit* passwordLineEdit;
    QPushButton* loginButton;

    iChatAPIClient* apiClient;
    Ui::LoginViewAdapter *ui;
};

#endif // LOGINVIEWADAPTER_H
