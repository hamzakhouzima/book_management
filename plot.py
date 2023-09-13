import mysql.connector
import matplotlib.pyplot as plt

host = "localhost"
user = "root"
password = ""
database = "book_library_4"

try:
    connection = mysql.connector.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )

    if connection.is_connected():
        print("Connected to MySQL database")

    cursor = connection.cursor()

    query = "SELECT isbn, status, COUNT(*) AS quantity FROM bookinstance GROUP BY isbn, status"
    cursor.execute(query)

    results = cursor.fetchall()

    # Separate data for plotting
    isbns = [row[0] for row in results]
    statuses = [row[1].strip() for row in results]  # Remove leading/trailing whitespace
    quantities = [row[2] for row in results]

    # Define a color map for each status
    status_colors = {
        'available': 'green',
        'borrowed': 'blue',
        'lost': 'red'
        # Add more statuses and colors as needed
    }

    # Create a list of colors corresponding to each status
    bar_colors = [status_colors[status] for status in statuses]

    # Create a bar chart with custom colors
    plt.figure(figsize=(10, 6))
    plt.bar(range(len(isbns)), quantities, tick_label=isbns, color=bar_colors)
    plt.xlabel("ISBN")
    plt.ylabel("Quantity")
    plt.title("Quantity of Books by ISBN and Status")
    plt.xticks(rotation=45, ha="right")

    # Add a legend for the status colors
    legend_labels = [plt.Rectangle((0, 0), 1, 1, color=status_colors[status], label=status) for status in set(statuses)]
    plt.legend(handles=legend_labels)

    plt.show()

except mysql.connector.Error as e:
    print("Error:", e)

finally:
    if connection.is_connected():
        cursor.close()
        connection.close()
        print("Connection closed")
