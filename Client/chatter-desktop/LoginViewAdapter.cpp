#include <QJsonObject>


#include "LoginViewAdapter.h"
#include "ui_loginviewadapter.h"

LoginViewAdapter::LoginViewAdapter(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::LoginViewAdapter)
{
    this->ui->setupUi(this);
    this->ui->loginMsgLabel->setVisible(false);

    this->apiClient = new iChatAPIClient();
    connect(this->apiClient, SIGNAL(loginCompleted(bool,const QString&)),
            this, SLOT(handleLoginCompleted(bool,const QString&)));

    this->usernameLineEdit = ui->usernameLineEdit;
    this->passwordLineEdit = ui->passwordLineEdit;
    this->passwordLineEdit->setEchoMode(QLineEdit::Password);

    this->loginButton = ui->loginButton;

    connect(this->loginButton, SIGNAL(pressed()),
            this, SLOT(handleLoginButtonPressed()));
}

LoginViewAdapter::~LoginViewAdapter()
{
    delete ui;
    delete this->apiClient;
}

void LoginViewAdapter::handleLoginButtonPressed() {
    QString username = this->usernameLineEdit->text();
    QString password = this->passwordLineEdit->text();

    if (username.isEmpty() || password.isEmpty()) {
        this->ui->loginMsgLabel->setVisible(true);
        return;
    }

    this->loginButton->setEnabled(false);
    this->apiClient->authenticateUser(username, password);
}

void LoginViewAdapter::handleLoginCompleted(bool status, const QString &data) {
    if (!status) this->loginButton->setEnabled(true);
}
