// Create Rule
document.getElementById('createButton').addEventListener('click', function() {
    const ruleName = document.getElementById('ruleName').value;
    const ruleString = document.getElementById('ruleString').value;

    if (!ruleName || !ruleString) {
        alert('Please enter both Rule Name and Rule.');
        return;
    }

    // Call API to create rule (example endpoint)
    fetch('http://localhost:8086/rules/create_rule', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            ruleName: ruleName,
            ruleString: ruleString,
        }),
    })
    .then(response => response.json())
    .then(data => {
        alert('Rule created successfully!');
        console.log('Success:', data);
    })
    .catch((error) => {
        console.error('Error:', error);
    });
});

// Modify Rule
document.getElementById('modifyButton').addEventListener('click', function() {
    const ruleName = document.getElementById('ruleName').value;
    const ruleString = document.getElementById('ruleString').value;

    if (!ruleName || !ruleString) {
        alert('Please enter both Rule Name and Rule.');
        return;
    }

    // Call API to modify rule (example endpoint)
    fetch(`http://localhost:8086/rules/update_rule/${encodeURIComponent(ruleName)}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            ruleString: ruleString,
        }),
    })
    .then(response => response.json())
    .then(data => {
        alert('Rule modified successfully!');
        console.log('Success:', data);
    })
    .catch((error) => {
        console.error('Error:', error);
    });
});

// Combine Rules
document.getElementById('combineButton').addEventListener('click', function() {
    const ruleNames = document.getElementById('ruleNames').value.split(',').map(name => name.trim());
    const combinedRuleName = document.getElementById('combinedRuleName').value.trim();
    const combineType = document.getElementById('combineType').value;

    if (ruleNames.length === 0 || !combinedRuleName) {
        alert('Please enter rule names and a combined rule name.');
        return;
    }

    // Call API to combine rules
    fetch('http://localhost:8086/rules/combine_rules', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            ruleNames: ruleNames,
            combinedRuleName: combinedRuleName,
            operator: combineType // Include the operator
        }),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        alert('Rules combined successfully!');
        console.log('Success:', data);
    })
    .catch((error) => {
        console.error('Error:', error);
        alert('Failed to combine rules: ' + error.message);
    });
});

// Fetch and display attributes when "Get Attributes" button is clicked
document.getElementById('fetchAttributesButton').addEventListener('click', function () {
    const ruleName = document.getElementById('evaluateRuleName').value.trim();

    if (!ruleName) {
        alert('Please enter a rule name.');
        return;
    }

    // Fetch attributes for the rule
    fetch(`http://localhost:8086/rules/${encodeURIComponent(ruleName)}/attributes`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(attributes => {
        // Clear previous attributes
        const attributesContainer = document.getElementById('attributesContainer');
        attributesContainer.innerHTML = '';

        // Create input fields for each attribute
        attributes.forEach(attribute => {
            const label = document.createElement('label');
            label.textContent = `${attribute}:`;
            const input = document.createElement('input');
            input.type = 'text';
            input.name = attribute;
            input.placeholder = `Enter value for ${attribute}`;
            input.required = true;

            attributesContainer.appendChild(label);
            attributesContainer.appendChild(input);
        });

        // Show the evaluate button
        document.getElementById('evaluateRuleButton').style.display = 'block';
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to fetch attributes: ' + error.message);
    });
});

// Evaluate the rule when the "Evaluate Rule" button is clicked
document.getElementById('evaluateRuleButton').addEventListener('click', function () {
    const ruleName = document.getElementById('evaluateRuleName').value.trim();
    const formData = new FormData(document.getElementById('evaluateForm'));

    if (!ruleName) {
        alert('Please enter a rule name.');
        return;
    }

    // Collect attribute values
    const data = {};
    formData.forEach((value, key) => {
        data[key] = value;
    });

    // Call the backend API to evaluate the rule
    fetch(`http://localhost:8086/rules/${encodeURIComponent(ruleName)}/evaluate_rule`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(result => {
        const resultMessage = document.getElementById('resultMessage');
        if (result.result) {
            resultMessage.textContent = 'Eligible';
            resultMessage.style.color = 'green';
        } else {
            resultMessage.textContent = 'Not Eligible';
            resultMessage.style.color = 'red';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to evaluate rule: ' + error.message);
    });
});
document.getElementById('deleteAttributeButton').addEventListener('click', function () {
    const ruleName = document.getElementById('deleteRuleName').value.trim();

    if (!ruleName) {
        alert('Please enter a rule name to delete.');
        return;
    }

    fetch(`http://localhost:8086/rules/${encodeURIComponent(ruleName)}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        if (response.status === 204) {
            alert('Rule deleted successfully');
        } else if (response.status === 404) {
            alert('Rule not found');
        } else {
            alert('Failed to delete rule');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error deleting rule: ' + error.message);
    });
});
// Fetch and display all rules
document.getElementById('getAllRulesButton').addEventListener('click', function () {
    fetch('http://localhost:8086/rules/all', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(rules => {
        const allRulesList = document.getElementById('allRulesList');
        allRulesList.innerHTML = ''; // Clear any previous list

        rules.forEach(rule => {
            const listItem = document.createElement('li');
            listItem.textContent = `Rule Name: ${rule.name}, Rule Definition: ${rule.ruleString}`;
            allRulesList.appendChild(listItem);
        });
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to fetch all rules: ' + error.message);
    });
});
document.getElementById('getAllRulesButton').addEventListener('click', function () {
    fetch('http://localhost:8086/rules/all', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(rules => {
        const allRulesList = document.getElementById('allRulesList');
        allRulesList.innerHTML = ''; // Clear any previous list

        rules.forEach(rule => {
            const listItem = document.createElement('li');
            
            // Create separate divs for rule name and rule string
            const ruleNameDiv = document.createElement('div');
            const ruleStringDiv = document.createElement('div');

            // Set content for each div and add custom classes for styling
            ruleNameDiv.innerHTML = `<strong>Rule Name:</strong> ${rule.name}`;
            ruleStringDiv.innerHTML = `<strong>Rule Definition:</strong> ${rule.ruleString}`;
            ruleNameDiv.classList.add('rule-name');
            ruleStringDiv.classList.add('rule-string');

            // Append the divs to the list item
            listItem.appendChild(ruleNameDiv);
            listItem.appendChild(ruleStringDiv);

            // Append list item to the list
            allRulesList.appendChild(listItem);
        });
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to fetch all rules: ' + error.message);
    });
});
