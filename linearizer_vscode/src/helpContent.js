module.exports = {
    getHelpContent
  }
  
  function getHelpContent() {
    html = `<!DOCTYPE html>
        <html lang="en">
        
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Linearizer</title>
          <style>
            body {
              position: relative;
              margin-top: 5%;
            }
        
            .form {
              display: flex;
              flex-direction: column;
              align-items: center;
              position: relative;
              height: 50%;
              width: 50%;
              
              text-align: center;
              position: absolute;
              top: 0;
              left: 0;
              bottom: 0;
              right: 0;
              margin: auto;
            }
            .text {
              align-items: left;
              text-align: left;
              align-content: space-around;
            }
            .option {
              margin-top: 2vh;
            }
          </style>
        </head>
        
        <body>
          <div class="form">
            <h1>Help</h1>
            <div class="text">
              <div class="option">
                <b>Path to repository:</b><br>
                Write your absolute path to ".git" file in this line.<br>
                Example: <i>C:\\examplePackage\\...\\.git</i>
              </div>
              <div class="option">
                <b>Name of linearised branch:</b><br>
                Write name of linearized branch in this line.<br>
                Example: <i>master</i>
              </div>
              <div class="option">
                <b>Start point: </b><br>
                Write hash of linearization's starting point in this line.<br>
                Example: <i>ex12ample3456hash</i>
              </div>
              <div class="option">
                <b>Checkbox</b><br>
                Choose additional options of lineatization.
              </div>
              <div class="option">
                <p>After all press button "Linearize" to start linearization. You need to fill out all three lines to start.</p>
              </div>
              </div>
          </div>
        </body>
        
        </html>`;
  
    return html;
  }