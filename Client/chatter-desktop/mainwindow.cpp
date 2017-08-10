#include "mainwindow.h"
#include "ui_mainwindow.h"

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    this->login_va = new LoginViewAdapter(this);

    this->setCentralWidget(this->login_va);
}

MainWindow::~MainWindow()
{
    delete ui;
}
