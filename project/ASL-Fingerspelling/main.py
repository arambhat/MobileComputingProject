import cv2
import os
import time
import threading
import mediapipe
from convert_to_csv import convert_to_csv
from frames_extractor import frameExtractor
from prediction import predict
from hand_frame_cropping import crop_hand_frame
from Naked.toolshed.shell import execute_js, muterun_js


# Path to the directory containing Video Files

alphabet_videos_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'alphabet_videos')
alphabet_frames_folder_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'alphabet_frames')
alphabet_hand_frames_folder_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'alphabet_hand_frames')

word_videos_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'word_videos')
word_frames_folder_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'word_frames')
word_hand_frames_folder_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'word_hand_frames')


if __name__=='__main__':

    # print("MENU: \n1. Process and predict ASL alphabets \n2.Process and predict ASL words \n3. Predict(if videos have been processed already)")
    print("MENU: \n1. Process and predict ASL alphabets \n2.Process and predict ASL words")
    option = input("Please select an option: ")

    if option == '1':

        frameExtractor(alphabet_videos_path, alphabet_frames_folder_path)
        # nFiles = len([file for file in os.path.listdir()])
        listOfVideos = [66]
        for alphabet in listOfVideos:
            frames_folder_path = os.path.join(alphabet_frames_folder_path, "{}/".format(chr(alphabet)))
            # success = execute_js('posenet.js', frames_folder_path)
            success = True
            if success:
                # convert_to_csv(frames_folder_path)
                cropped_folder_path = os.path.join(alphabet_hand_frames_folder_path, "{}_cropped".format(chr(alphabet)))
                crop_hand_frame_alphabets(frames_folder_path, cropped_folder_path)

        predict(alphabet_video_path = alphabet_videos_path, alphabet_frame_path= alphabet_hand_frames_folder_path)

    elif option == '2':

        thread = threading.Thread(target=frameExtractor(word_videos_path, word_frames_folder_path))
        thread.start()
        thread.join()

        video_files_names =  [file for file in os.listdir(word_videos_path) if file.endswith('.mp4')]
        for file_name in video_files_names:
            word_name = file_name.split('.')[0]
            print("Key points file generation for word {}".format(file_name.split('.')[0]))
            frames_folder_path = os.path.join(word_frames_folder_path, "{}/".format(word_name))
            success = execute_js('posenet.js', frames_folder_path)
            success=True
            if success:
                convert_to_csv(frames_folder_path)
                cropped_folder_path = os.path.join(word_hand_frames_folder_path, "{}_cropped".format(word_name))
                crop_hand_frame_words(frames_folder_path, cropped_folder_path)

        predict(word_video_path= word_videos_path, word_frame_path=word_hand_frames_folder_path, pos_key_path=word_frames_folder_path)

