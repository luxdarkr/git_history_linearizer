const vscode = require('vscode');
module.exports = {
    helpFunc,
    linFunc,
    fixFunc,
    hello,
    help,
    linearize
}

function helpFunc() {
    vscode.window.showInformationMessage('Help is already on its way.');
};
function linFunc(rep, start, branch) {
    if (rep == '' || start == '' || branch == '')
        vscode.window.showInformationMessage('Fix your lines')
    else
        vscode.window.showInformationMessage(' Repository: ' + rep +
            ' Start: ' + start +
            ' Branch: ' + branch);
};
function fixFunc(rep, start, branch) {
    if (rep == '' || start == '' || branch == '')
        vscode.window.showInformationMessage('Fix your lines')
    else
        vscode.window.showInformationMessage('Your stuff was fixed');
};
function hello() {
    vscode.window.showInformationMessage('Hello!');
}

function help() {
    const { exec } = require("child_process");

    exec("Linearizer -h", (error, stdout, stderr) => {
        if (error) {
            vscode.window.showInformationMessage(`error: ${error.message}`);
            return;
        }
        if (stderr) {
            vscode.window.showInformationMessage(`${stderr}`);
            return;
        }
        vscode.window.showInformationMessage(`${stdout}`);
    });
}

function linearize(rep, start, branch) {
    const { exec } = require("child_process");
    exec("Linearizer -l", (error, stdout, stderr) => {
        if (error) {
            vscode.window.showInformationMessage(`error: ${error.message}`);
            return;
        }
        if (stderr) {
            vscode.window.showInformationMessage(`${stderr}`);
            return;
        }
        vscode.window.showInformationMessage(`${stdout}`);
    });
}