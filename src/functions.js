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
};
function linFunc(rep, start, branch) {
    const { exec } = require("child_process");
    if (rep == '' || start == '' || branch == '')
        vscode.window.showInformationMessage('Fix your lines')
    else
    exec("Linearizer -l " + rep + branch + start, (error, stdout, stderr) => {
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
};
function fixFunc(rep, start, branch) {
    const { exec } = require("child_process");
    if (rep == '' || start == '' || branch == '')
        vscode.window.showInformationMessage('Fix your lines')
    else
    exec("Linearizer -l " + rep + branch + start + "-f", (error, stdout, stderr) => {
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
    exec("Linearizer -l" + rep + branch + start, (error, stdout, stderr) => {
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