const vscode = require('vscode');
module.exports = {
    helpFunc,
    linFunc,
    hello,
    help,
    linearize
}

function helpFunc() {
    const { exec } = require("child_process");

exec("Linearizer -h ", (error, stdout, stderr) => {
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
function linFunc(rep, start, branch, strip, fix_case, remove, fix_messages) {
    let form = "Linearizer -l " + rep + " " + branch + " " + start
    const { exec } = require("child_process")
    if (rep == '' || start == '' || branch == '')
        vscode.window.showInformationMessage('Fix your lines')
    // else {
    //     exec(form, (error, stdout, stderr) => {
    //         if (error) {
    //             vscode.window.showInformationMessage(`error: ${error.message}`);
    //             return;
    //         }
    //         if (stderr) {
    //             vscode.window.showInformationMessage(`${stderr}`);
    //             return;
    //         }
    //         vscode.window.showInformationMessage(`${stdout}`);
    //     });
        if (strip == true) {form = form + " " + "-s"}
        // exec(form = form + " " + "-s", (error, stdout, stderr) => {
        //     if (error) {
        //         vscode.window.showInformationMessage(`error: ${error.message}`);
        //         return;
        //     }
        //     if (stderr) {
        //         vscode.window.showInformationMessage(`${stderr}`);
        //         return;
        //     }
        //     vscode.window.showInformationMessage(`strip is on ${stdout}`);
        // });
        // vscode.window.showInformationMessage('strip is on')
        if (fix_case == true) {form = form + " " + "-f"}
        // exec(form = form + " " + "-f", (error, stdout, stderr) => {
        //     if (error) {
        //         vscode.window.showInformationMessage(`error: ${error.message}`);
        //         return;
        //     }
        //     if (stderr) {
        //         vscode.window.showInformationMessage(`${stderr}`);
        //         return;
        //     }
        //     vscode.window.showInformationMessage(`fix_case is on ${stdout}`);
        // });
        // vscode.window.showInformationMessage('fix_case is on')
        if (remove == true) {form = form + " " + "-r"}
        // exec(form = form + " " + "-r", (error, stdout, stderr) => {
        //     if (error) {
        //         vscode.window.showInformationMessage(`error: ${error.message}`);
        //         return;
        //     }
        //     if (stderr) {
        //         vscode.window.showInformationMessage(`${stderr}`);
        //         return;
        //     }
        //     vscode.window.showInformationMessage(`remove is on ${stdout}`);
        // });
        //vscode.window.showInformationMessage('remove is on')
        if (fix_messages == true) {form = form + " " + "-b"}
        exec(form, (error, stdout, stderr) => {
            if (error) {
                vscode.window.showInformationMessage(`error: ${error.message}`);
                return;
            }
            if (stderr) {
                vscode.window.showInformationMessage(`${stderr}`);
                return;
            }
            vscode.window.showInformationMessage(`Success! ${stdout}`);
        });
        // //vscode.window.showInformationMessage('fix_messages is on')
    }
    
function hello() {
    vscode.window.showInformationMessage('Hello!');
}

function help() {
    const { exec } = require("child_process");

    exec("Linearizer -h ", (error, stdout, stderr) => {
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